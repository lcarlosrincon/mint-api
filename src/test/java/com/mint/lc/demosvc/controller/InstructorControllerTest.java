package com.mint.lc.demosvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mint.lc.demosvc.repository.InstructorRepository;
import com.mint.lc.demosvc.repository.model.Event;
import com.mint.lc.demosvc.repository.model.Instructor;
import com.mint.lc.demosvc.service.EventRequest;
import com.mint.lc.demosvc.service.EventService;
import com.mint.lc.demosvc.service.ExternalEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(InstructorController.class)
class InstructorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstructorRepository instructorRepository;

    @MockBean
    private EventService eventService;

    @MockBean
    private ExternalEventService externalEventService;

    @InjectMocks
    private InstructorController instructorController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllInstructors() throws Exception {
        List<Instructor> instructors = Arrays.asList(new Instructor(), new Instructor());
        when(instructorRepository.findAll()).thenReturn(instructors);

        mockMvc.perform(MockMvcRequestBuilders.get("/instructors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(instructors.size()));
    }

    @Test
    void testGetInstructorById() throws Exception {
        Instructor instructor = new Instructor();
        instructor.setId("1");
        when(instructorRepository.findById(anyString())).thenReturn(Optional.of(instructor));

        mockMvc.perform(MockMvcRequestBuilders.get("/instructors/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(instructor.getId()));
    }

    @Test
    void testGetEventsByInstructorId() throws Exception {
        List<Event> events = Collections.singletonList(new Event());
        when(eventService.getEventsById(anyString(), anyString())).thenReturn(events);

        mockMvc.perform(MockMvcRequestBuilders.get("/instructors/1/events")
                        .param("month", "2024-05")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(events.size()));
    }

    @Test
    void testSaveEvent() throws Exception {
        Event event = new Event();
        EventRequest eventRequest = new EventRequest(LocalDate.now(), LocalDate.now(), "Test", "abc");
        when(eventService.save(anyString(), any(EventRequest.class))).thenReturn(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/instructors/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testDeleteEvent() throws Exception {
        Event event = new Event();
        when(eventService.deleteEvent(anyString(), any(UUID.class))).thenReturn(event);

        mockMvc.perform(MockMvcRequestBuilders.delete("/instructors/1/events/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    void testUpdateEvent() throws Exception {
        Event event = new Event();
        when(eventService.updateEvent(anyString(), any(UUID.class), any(EventRequest.class))).thenReturn(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/instructors/1/events/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void givenInvalidUUID_whenUpdateEvent_then400IsExpected() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/instructors/1/events/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void givenUnexpectedError_whenUpdateEvent_then500IsExpected() throws Exception {
        when(eventService.updateEvent(anyString(), any(UUID.class), any(EventRequest.class))).thenThrow(RuntimeException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/instructors/1/events/"+UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    void testGivenInstructorNotFound_whenUpdateEvent_thenCheckNotFoundStatus() throws Exception {
        when(eventService.updateEvent(anyString(), any(UUID.class), any(EventRequest.class))).thenThrow(NoSuchElementException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/instructors/1/events/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testSaveExternalEvent() throws Exception {
        Event event = new Event();
        when(externalEventService.saveExternals(anyString(), anyString())).thenReturn(List.of(event));

        mockMvc.perform(MockMvcRequestBuilders.post("/instructors/1/events/externals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("month", "2024-05"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

}
