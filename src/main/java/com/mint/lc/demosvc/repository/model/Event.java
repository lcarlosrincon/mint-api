package com.mint.lc.demosvc.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "events")
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String instructorId;
    @ManyToOne
    @JoinColumn(name = "event_type_id")
    private EventType eventType;
    private String externalId;
}
