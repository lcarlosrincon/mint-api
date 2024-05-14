package com.mint.lc.demosvc.service;

import com.google.api.client.util.Lists;
import com.mint.lc.demosvc.repository.EventRepository;
import com.mint.lc.demosvc.repository.EventTypeRepository;
import com.mint.lc.demosvc.repository.ExternalCalendarRepository;
import com.mint.lc.demosvc.repository.InstructorRepository;
import com.mint.lc.demosvc.repository.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalEventServiceImpl implements ExternalEventService {

    @Autowired
    private final InstructorRepository instructorRepository;

    @Autowired
    private final EventRepository eventRepository;

    @Autowired
    private final EventTypeRepository eventTypeRepository;

    @Autowired(required = false)
    private final List<ExternalCalendarRepository> externalCalendarRepositories;

    @Override
    public List<Event> saveExternals(String instructorId, String month) {
        this.instructorRepository.findById(instructorId).orElseThrow(() -> new NoSuchElementException("Instructor not found"));
        List<Event> externalEvents = Lists.newArrayList();
        if (this.externalCalendarRepositories == null)
            return externalEvents;
        for (ExternalCalendarRepository repo : this.externalCalendarRepositories) {
            try {
                externalEvents.addAll(repo.getEvents(instructorId,
                        YearMonth.parse(month, DateTimeFormatter.ofPattern(EventService.MONTH_PARAM_FORMAT))));
                log.info(repo + ": Events found: " + externalEvents.size());
            } catch (IOException e) {
                log.error("Error getting events from " + repo, e);
            }
        }
        List<Event> saved = Lists.newArrayList();
        for (Event event : externalEvents) {
            event.setInstructorId(instructorId);
            Event savedEvent = this.eventRepository.findByExternalId(event.getExternalId())
                    .map(found -> updateExternalEvent(found, event))
                    .orElseGet(() -> {
                        Event newEvent = event.getEventType() != null ?
                                event.toBuilder()
                                        .eventType(this.eventTypeRepository.findById(event.getEventType().getId()).orElse(null))
                                        .build() :
                                event;
                        return this.eventRepository.save(newEvent);
                    });
            saved.add(savedEvent);
            log.info("Event processed:" + savedEvent);
        }
        return saved;
    }

    private Event updateExternalEvent(Event found, Event externalEvent) {
        Event updated = found.toBuilder().description(externalEvent.getDescription())
                .startDate(externalEvent.getStartDate())
                .endDate(externalEvent.getEndDate())
                .build();
        return this.eventRepository.save(updated);
    }

}
