package com.mint.lc.demosvc.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventType {

    public static final String GOOGLE_CALENDAR_ID = "googleType";
    public static final String MINT_ID = "mintType";

    @Id
    private String id;
    private String name;
}
