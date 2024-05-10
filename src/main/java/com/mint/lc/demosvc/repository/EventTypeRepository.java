package com.mint.lc.demosvc.repository;

import com.mint.lc.demosvc.repository.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType, String> {
}
