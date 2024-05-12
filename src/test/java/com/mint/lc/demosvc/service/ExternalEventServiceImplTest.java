package com.mint.lc.demosvc.service;

import com.mint.lc.demosvc.repository.EventRepository;
import com.mint.lc.demosvc.repository.EventTypeRepository;
import com.mint.lc.demosvc.repository.ExternalCalendarRepository;
import com.mint.lc.demosvc.repository.InstructorRepository;
import com.mint.lc.demosvc.repository.model.Event;
import com.mint.lc.demosvc.repository.model.EventType;
import com.mint.lc.demosvc.repository.model.Instructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExternalEventServiceImplTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventTypeRepository eventTypeRepository;

    @Mock
    private List<ExternalCalendarRepository> externalCalendarRepositories;

    @InjectMocks
    private ExternalEventServiceImpl externalEventService;

    @Test
    public void givenInstructorNotFound_whenSaveExternals_thenThrowNoSuchElementException() throws IOException {
        String instructorId = "instructor123";
        String month = "2024-05";
        when(instructorRepository.findById(instructorId)).thenReturn(java.util.Optional.empty());
        assertThrows(NoSuchElementException.class, () -> {
            externalEventService.saveExternals(instructorId, month);
        });
    }

    @Test
    public void givenIOExceptionFromExternalCalendarRepository_whenSaveExternals_thenHandleException() throws IOException {
        String instructorId = "instructor123";
        String month = "2024-05";
        when(instructorRepository.findById(instructorId)).thenReturn(java.util.Optional.of(new Instructor()));
        ExternalCalendarRepository externalCalendarRepository = mock(ExternalCalendarRepository.class);
        when(externalCalendarRepository.getEvents(eq(instructorId), any())).thenThrow(IOException.class);
        when(externalCalendarRepositories.iterator()).thenReturn(Collections.singletonList(externalCalendarRepository).iterator());
        List<Event> savedEvents = externalEventService.saveExternals(instructorId, month);
        assertTrue(savedEvents.isEmpty());
        verify(instructorRepository).findById(instructorId);
        verify(externalCalendarRepository).getEvents(eq(instructorId), any());
    }

    @Test
    public void givenEventWithSameExternalIdExists_whenSaveExternals_thenDoNotSaveEventAgain() throws IOException {
        String instructorId = "instructor123";
        String month = "2024-05";
        when(instructorRepository.findById(instructorId)).thenReturn(java.util.Optional.of(new Instructor()));
        ExternalCalendarRepository externalCalendarRepository = mock(ExternalCalendarRepository.class);
        Event existingEvent = new Event();
        existingEvent.setExternalId("existingEventId");
        when(eventRepository.findByExternalId(eq(existingEvent.getExternalId()))).thenReturn(java.util.Optional.of(existingEvent));
        when(eventRepository.findByExternalId(AdditionalMatchers.not(eq(existingEvent.getExternalId())))).thenReturn(java.util.Optional.empty());
        when(eventRepository.save(any())).thenReturn(existingEvent);
        Event toSave = Event.builder().eventType(EventType.builder().id(EventType.GOOGLE_CALENDAR_ID).build()).build();
        when(externalCalendarRepository.getEvents(eq(instructorId), any())).thenReturn(List.of(toSave, existingEvent));
        when(externalCalendarRepositories.iterator()).thenReturn(Collections.singletonList(externalCalendarRepository).iterator());

        List<Event> savedEvents = externalEventService.saveExternals(instructorId, month);

        assertEquals(2, savedEvents.size());
        verify(eventRepository, times(2)).save(any());
    }

}
