package com.mint.lc.demosvc.service;

import com.mint.lc.demosvc.repository.model.Event;

import java.util.List;
import java.util.UUID;

public interface EventService {

    public static final String MONTH_PARAM_FORMAT = "yyyy-MM";

    Event save(String instructorId, EventRequest request);

    List<Event> getEventsById(String instructorId, String month);

    Event deleteEvent(String instructorId, UUID eventId);

    Event updateEvent(String instructorId, UUID eventId, EventRequest request);

}
