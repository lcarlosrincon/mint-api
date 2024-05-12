package com.mint.lc.demosvc.repository;

import com.mint.lc.demosvc.repository.model.Event;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

public interface ExternalCalendarRepository {

    List<Event> getEvents(String email, YearMonth month) throws IOException;
}
