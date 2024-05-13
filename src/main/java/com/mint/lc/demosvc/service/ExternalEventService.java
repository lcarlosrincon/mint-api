package com.mint.lc.demosvc.service;

import com.mint.lc.demosvc.repository.model.Event;

import java.util.List;

public interface ExternalEventService {

    List<Event> saveExternals(String instructorId, String month);
}
