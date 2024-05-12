package com.mint.lc.demosvc.service;

import com.mint.lc.demosvc.repository.InstructorRepository;
import com.mint.lc.demosvc.repository.model.Instructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogServiceImpl implements LogService {


    @Autowired
    private final InstructorRepository instructorRepository;

    @Override
    public Instructor logIn(LogRequest logRequest) {
        return instructorRepository.findById(
                logRequest.username()).orElseThrow(() -> new UnauthenticatedUserException("Instructor not found"));
    }

    @Override
    public void logOut(String username) {

    }
}
