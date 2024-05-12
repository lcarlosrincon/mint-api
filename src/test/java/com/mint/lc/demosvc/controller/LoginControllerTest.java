package com.mint.lc.demosvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mint.lc.demosvc.repository.model.Instructor;
import com.mint.lc.demosvc.service.LogRequest;
import com.mint.lc.demosvc.service.LogService;
import com.mint.lc.demosvc.service.UnauthenticatedUserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogService logService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenValidLogRequest_whenLogIn_thenReturnInstructor() throws Exception {
        // Arrange
        LogRequest logRequest = new LogRequest("testUser", "any");
        Instructor instructor = new Instructor();
        instructor.setId("123");
        when(logService.logIn(logRequest)).thenReturn(instructor);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(instructor.getId()));
    }

    @Test
    public void givenInvalidLogRequest_whenLogIn_thenReturn401() throws Exception {
        // Arrange
        LogRequest logRequest = new LogRequest("testUser", "any");
        when(logService.logIn(logRequest)).thenThrow(new UnauthenticatedUserException("User not found test"));

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenUserId_whenLogOut_thenInvokeLogOut() throws Exception {
        String userId = "testUser";

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/logout/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(logService, times(1)).logOut(userId);
    }
}
