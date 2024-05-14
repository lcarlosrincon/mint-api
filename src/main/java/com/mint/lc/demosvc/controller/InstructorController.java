package com.mint.lc.demosvc.controller;

import com.mint.lc.demosvc.repository.InstructorRepository;
import com.mint.lc.demosvc.repository.model.Event;
import com.mint.lc.demosvc.repository.model.Instructor;
import com.mint.lc.demosvc.service.EventRequest;
import com.mint.lc.demosvc.service.EventService;
import com.mint.lc.demosvc.service.ExternalEventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/instructors")
@RequiredArgsConstructor
@Slf4j
public class InstructorController {

    @Autowired
    private final InstructorRepository instructorRepository;

    @Autowired
    private final EventService eventService;

    @Autowired
    private final ExternalEventService externalEventService;

    @GetMapping
    public List<Instructor> getAll() {
        return this.instructorRepository.findAll();
    }

    @GetMapping("/{instructorId}")
    public Instructor getById(@PathVariable("instructorId") String id) {
        return this.instructorRepository.findById(id).orElseThrow();
    }

    @GetMapping("/{instructorId}/events")
    public List<Event> getEventsById(@PathVariable("instructorId") String instructorId,
                                     @RequestParam(value = "month", required = false) String month) {
        return this.eventService.getEventsById(instructorId, month);
    }

    @PostMapping("/{instructorId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Event save(@PathVariable("instructorId") String instructorId,
                      @RequestBody EventRequest request) {
        log.info("An event will be created " + request);
        return this.eventService.save(instructorId, request);
    }

    @Operation(description = "Through this endpoint, the api will be fetching events from external calendars like google calendar")
    @PostMapping("/{instructorId}/events/externals")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Event> saveExternals(@PathVariable("instructorId") String instructorId,
                               @RequestParam(value = "month") String month) {
        log.info("Events will be created for externals at " + month);
        return this.externalEventService.saveExternals(instructorId, month);
    }

    @DeleteMapping("/{instructorId}/events/{eventId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Event deleteEvent(@PathVariable("instructorId") String instructorId,
                             @PathVariable("eventId") String eventId) {
        return this.eventService.deleteEvent(instructorId, UUID.fromString(eventId));
    }

    @PutMapping("/{instructorId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Event updateEvent(@PathVariable("instructorId") String instructorId,
                             @PathVariable("eventId") String eventId,
                             @RequestBody EventRequest request) {
        return this.eventService.updateEvent(instructorId, UUID.fromString(eventId), request);
    }

}
