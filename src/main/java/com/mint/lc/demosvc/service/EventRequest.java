package com.mint.lc.demosvc.service;

import java.time.LocalDate;

public record EventRequest(LocalDate startDate, LocalDate endDate, String description, String eventType) {
}
