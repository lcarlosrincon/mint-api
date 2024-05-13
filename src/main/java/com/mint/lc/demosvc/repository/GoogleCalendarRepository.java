package com.mint.lc.demosvc.repository;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;
import com.mint.lc.demosvc.repository.model.Event;
import com.mint.lc.demosvc.repository.model.EventType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class GoogleCalendarRepository implements ExternalCalendarRepository {

    private static final String APPLICATION_NAME = "proyecto-prueba-275003";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        InputStream in = GoogleCalendarRepository.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8081).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Calendar service;

    @PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
    }

    @Override
    public List<Event> getEvents(String email, YearMonth month) throws IOException {
        List<Event> eventList = Lists.newArrayList();
        String pageToken = null;
        do {
            Events events = service.events().list("primary")
                    .setTimeMin(new DateTime(Date.from(month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant())))
                    .setTimeMax(new DateTime(Date.from(month.atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())))
                    .setPageToken(pageToken).execute();
            List<com.google.api.services.calendar.model.Event> items = events.getItems();
            log.info("Events found in google calendar:" + items);
            for (com.google.api.services.calendar.model.Event event : items) {
                Event eventFound = convert(event);
                if (eventFound != null)
                    eventList.add(eventFound);
            }
            pageToken = events.getNextPageToken();
        } while (pageToken != null);
        return eventList;
    }

    private Event convert(com.google.api.services.calendar.model.Event event) {
        if (event.getStart() == null || event.getEnd() == null) {
            log.warn("Event has not defined dates " + event.getId());
            return null;
        }
        DateTime startDate = event.getStart().getDateTime();
        startDate = startDate == null ? event.getStart().getDate() : startDate;
        DateTime endDate = event.getEnd().getDateTime();
        endDate = endDate == null ? event.getEnd().getDate() : endDate;
        return Event.builder()
                .startDate(new Date(startDate.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .endDate(new Date(endDate.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .description(event.getSummary())
                .externalId(event.getId())
                .eventType(EventType.builder().id(EventType.GOOGLE_CALENDAR_ID).build())
                .build();
    }
}
