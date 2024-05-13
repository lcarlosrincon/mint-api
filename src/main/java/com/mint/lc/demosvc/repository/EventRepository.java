package com.mint.lc.demosvc.repository;

import com.mint.lc.demosvc.repository.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, String> {
    List<Event> findByInstructorIdEquals(String instructorId);

    @Query("select event from Event event where event.instructorId=?1 and event.startDate between ?2 and ?3")
    List<Event> findByInstructorAndDate(String instructorId, LocalDate startDate, LocalDate endDate);

    Optional<Event> findByIdAndInstructorId(UUID eventId, String instructorId);

    Optional<Event> findByExternalId(String externalId);
}
