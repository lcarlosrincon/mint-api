package com.mint.lc.demosvc.controller;

import com.mint.lc.demosvc.repository.model.Instructor;
import com.mint.lc.demosvc.service.LogRequest;
import com.mint.lc.demosvc.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    @Autowired
    private final LogService logService;

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public Instructor logIn(@RequestBody LogRequest logRequest) {
        return this.logService.logIn(logRequest);
    }

    @PostMapping("/logout/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logOut(@PathVariable("userId") String userId) {
        this.logService.logOut(userId);
    }

}
