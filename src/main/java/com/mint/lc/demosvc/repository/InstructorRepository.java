package com.mint.lc.demosvc.repository;

import com.mint.lc.demosvc.repository.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, String> {
}
