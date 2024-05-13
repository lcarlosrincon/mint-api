package com.mint.lc.demosvc.repository;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.mint.lc.demosvc.repository.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoogleCalendarRepositoryTest {

    @Mock
    private Calendar service;

    @InjectMocks
    private GoogleCalendarRepository googleCalendarRepository;

    @BeforeEach
    public void givenCalendarServiceInitialized() {
        // Mock any necessary behavior of the service
    }

    @Test
    public void whenGetEventsNoDates_thenRetrieveEventsFromGoogleCalendar() throws IOException {

        mockService(List.of(
                new com.google.api.services.calendar.model.Event().setSummary("Event 1"),
                new com.google.api.services.calendar.model.Event().setSummary("Event 2")
        ));

        List<Event> events = googleCalendarRepository.getEvents("test@example.com", YearMonth.of(2022, 1));

        assertEquals(0, events.size());
    }

    private void mockService(List<com.google.api.services.calendar.model.Event> param) throws IOException {
        Calendar.Events eventList = Mockito.spy(service.new Events());
        Calendar.Events.List list = Mockito.spy(eventList.list("x"));
        when(service.events()).thenReturn(eventList);
        doReturn(list).when(eventList).list(any());
        Events events1 = Mockito.mock(Events.class);
        doReturn(events1).when(list).execute();
        when(events1.getItems()).thenReturn(param);
    }

    @Test
    public void whenGetEventsWithDateTime_thenRetrieveEventsFromGoogleCalendar() throws IOException {
        mockService(List.of(
                        new com.google.api.services.calendar.model.Event().setSummary("Event with DateTime")
                                .setStart(new EventDateTime().setDateTime(new DateTime("2022-01-01T12:00:00Z")))
                                .setEnd(new EventDateTime().setDateTime(new DateTime("2022-01-01T14:00:00Z")))
                ));

        List<Event> events = googleCalendarRepository.getEvents("test@example.com", YearMonth.of(2022, 1));

        assertEquals(1, events.size());
        assertEquals("Event with DateTime", events.get(0).getDescription());
        assertEquals("2022-01-01", events.get(0).getStartDate().toString());
        assertEquals("2022-01-01", events.get(0).getEndDate().toString());
    }

}
