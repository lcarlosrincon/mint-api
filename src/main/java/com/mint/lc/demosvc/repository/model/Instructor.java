package com.mint.lc.demosvc.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "instructors")
@Data
public class Instructor {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
}
