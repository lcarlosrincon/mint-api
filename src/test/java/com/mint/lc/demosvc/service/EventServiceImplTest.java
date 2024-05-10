package com.mint.lc.demosvc.service;

import com.mint.lc.demosvc.repository.EventRepository;
import com.mint.lc.demosvc.repository.EventTypeRepository;
import com.mint.lc.demosvc.repository.InstructorRepository;
import com.mint.lc.demosvc.repository.model.Event;
import com.mint.lc.demosvc.repository.model.EventType;
import com.mint.lc.demosvc.repository.model.Instructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventTypeRepository eventTypeRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void givenInstructorAndEventRequest_whenSaveEvent_thenEventIsSaved() {
        // Given
        String instructorId = "1";
        EventRequest request = new EventRequest(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Test Event",
                "1"
        );

        Instructor instructor = new Instructor();
        instructor.setId(instructorId);
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        EventType eventType = new EventType();
        eventType.setId("1");
        when(eventTypeRepository.findById(request.eventType())).thenReturn(Optional.of(eventType));

        Event savedEvent = new Event();
        savedEvent.setId(UUID.randomUUID());
        savedEvent.setInstructorId(instructorId);
        savedEvent.setDescription(request.description());
        savedEvent.setStartDate(request.startDate());
        savedEvent.setEndDate(request.endDate());
        savedEvent.setEventType(eventType);
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        // When
        Event createdEvent = eventService.save(instructorId, request);

        // Then
        assertNotNull(createdEvent);
        assertEquals(instructorId, createdEvent.getInstructorId());
        assertEquals(request.description(), createdEvent.getDescription());
        assertEquals(request.startDate(), createdEvent.getStartDate());
        assertEquals(request.endDate(), createdEvent.getEndDate());
        assertEquals(eventType, createdEvent.getEventType());
    }

    @Test
    void givenInstructorAndMonth_whenGetEventsById_thenEventsAreReturned() {
        // Given
        String instructorId = "1";
        String month = "2023-01";
        LocalDate startDate = YearMonth.parse(month, DateTimeFormatter.ofPattern(EventService.MONTH_PARAM_FORMAT)).atDay(1);
        LocalDate endDate = YearMonth.parse(month, DateTimeFormatter.ofPattern(EventService.MONTH_PARAM_FORMAT)).atEndOfMonth();

        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setInstructorId(instructorId);

        when(eventRepository.findByInstructorAndDate(instructorId, startDate, endDate)).thenReturn(Collections.singletonList(event));

        // When
        List<Event> events = eventService.getEventsById(instructorId, month);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
        verify(eventRepository, never()).findByInstructorIdEquals(anyString());
    }

    @Test
    void givenInstructor_whenGetEventsById_thenEventsAreReturned() {
        // Given
        String instructorId = "1";

        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setInstructorId(instructorId);

        when(eventRepository.findByInstructorIdEquals(instructorId)).thenReturn(Collections.singletonList(event));

        // When
        List<Event> events = eventService.getEventsById(instructorId, null);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
        verify(eventRepository, never()).findByInstructorAndDate(anyString(), any(), any());
    }

    @Test
    void givenInstructorAndExistingEventId_whenDeleteEvent_thenEventIsDeleted() {
        // Given
        String instructorId = "1";
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);
        event.setInstructorId(instructorId);
        when(eventRepository.findByIdAndInstructorId(eventId, instructorId)).thenReturn(Optional.of(event));

        // When
        Event deletedEvent = eventService.deleteEvent(instructorId, eventId);

        // Then
        assertNotNull(deletedEvent);
        assertEquals(event, deletedEvent);
    }

    @Test
    void givenInstructorAndExistingEventIdAndRequest_whenUpdateEvent_thenEventIsUpdated() {
        // Given
        String instructorId = "1";
        UUID eventId = UUID.randomUUID();
        EventRequest request = new EventRequest(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                "Updated Event",
                null
        );

        Event existingEvent = new Event();
        existingEvent.setId(eventId);
        existingEvent.setInstructorId(instructorId);
        existingEvent.setDescription("Old Event");
        existingEvent.setStartDate(LocalDate.now());
        existingEvent.setEndDate(LocalDate.now().plusDays(1));
        when(eventRepository.findByIdAndInstructorId(eventId, instructorId)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // When
        Event updatedEvent = eventService.updateEvent(instructorId, eventId, request);

        // Then
        assertNotNull(updatedEvent);
        assertEquals(instructorId, updatedEvent.getInstructorId());
        assertEquals(request.description(), updatedEvent.getDescription());
        assertEquals(request.startDate(), updatedEvent.getStartDate());
        assertEquals(request.endDate(), updatedEvent.getEndDate());
    }
}
