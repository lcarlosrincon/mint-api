package com.mint.lc.demosvc.service;

import com.mint.lc.demosvc.repository.EventRepository;
import com.mint.lc.demosvc.repository.EventTypeRepository;
import com.mint.lc.demosvc.repository.InstructorRepository;
import com.mint.lc.demosvc.repository.model.Event;
import com.mint.lc.demosvc.repository.model.EventType;
import com.mint.lc.demosvc.repository.model.Instructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {


    @Autowired
    private final InstructorRepository instructorRepository;

    @Autowired
    private final EventRepository eventRepository;

    @Autowired
    private final EventTypeRepository eventTypeRepository;

    public Event save(String instructorId, EventRequest request) {
        Optional<Instructor> instructor = this.instructorRepository.findById(instructorId);
        Event event = instructor.map(instructor1 -> createEvent(instructorId, request)).orElseThrow();
        return this.eventRepository.save(event);
    }

    private Event createEvent(String instructorId, EventRequest request) {
        return Event.builder()
                .instructorId(instructorId)
                .description(request.description())
                .endDate(request.endDate())
                .startDate(request.startDate())
                .eventType(this.eventTypeRepository.findById(request.eventType() != null ?
                                request.eventType() : EventType.MINT_ID)
                        .orElseThrow(() -> new NoSuchElementException("Event type not found")))
                .build();
    }

    public List<Event> getEventsById(String instructorId, String month) {
        if (StringUtils.isNotBlank(month)) {
            LocalDate startDate = YearMonth.parse(month, DateTimeFormatter.ofPattern(MONTH_PARAM_FORMAT)).atDay(1);
            LocalDate endDate = YearMonth.parse(month, DateTimeFormatter.ofPattern(MONTH_PARAM_FORMAT)).atEndOfMonth();
            return this.eventRepository.findByInstructorAndDate(instructorId, startDate, endDate);
        } else {
            return this.eventRepository.findByInstructorIdEquals(instructorId);
        }
    }

    public Event deleteEvent(String instructorId, UUID eventId) {
        return this.eventRepository.findByIdAndInstructorId(eventId, instructorId).orElseThrow();
    }

    public Event updateEvent(String instructorId, UUID eventId, EventRequest request) {
        return this.eventRepository.findByIdAndInstructorId(eventId, instructorId)
                .map(event -> this.eventRepository.save(updateEvent(event, request)))
                .orElseThrow();
    }

    private Event updateEvent(Event event, EventRequest request) {
        event.setDescription(request.description());
        event.setStartDate(request.startDate());
        event.setEndDate(request.endDate());
        return event;
    }
}
