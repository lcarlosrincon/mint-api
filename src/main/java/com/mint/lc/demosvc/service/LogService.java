package com.mint.lc.demosvc.service;

import com.mint.lc.demosvc.repository.model.Instructor;

public interface LogService {

    Instructor logIn(LogRequest logRequest);

    void logOut(String username);

}
