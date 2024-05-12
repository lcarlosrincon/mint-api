package com.mint.lc.demosvc.service;

import com.mint.lc.demosvc.repository.InstructorRepository;
import com.mint.lc.demosvc.repository.model.Instructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceImplTest {

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private LogServiceImpl logService;

    @Test
    public void givenValidUsername_whenLogIn_thenReturnInstructor() {
        String username = "testUser";
        LogRequest logRequest = new LogRequest(username, "any");
        Instructor instructor = new Instructor();
        when(instructorRepository.findById(username)).thenReturn(Optional.of(instructor));

        Instructor result = logService.logIn(logRequest);

        assertEquals(instructor, result);
        verify(instructorRepository, times(1)).findById(username);
    }

    @Test
    public void givenInvalidUsername_whenLogIn_thenThrowUnauthenticatedUserException() {
        String username = "nonExistentUser";
        LogRequest logRequest = new LogRequest(username, "any");
        when(instructorRepository.findById(username)).thenReturn(Optional.empty());

        assertThrows(UnauthenticatedUserException.class, () -> logService.logIn(logRequest));
        verify(instructorRepository, times(1)).findById(username);
    }

}