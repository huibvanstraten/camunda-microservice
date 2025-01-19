package com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.web.rest;

import com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.service.NewReleasesProcessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewReleasesResourceTest {

    @Mock
    private NewReleasesProcessService newReleasesProcessService;

    @InjectMocks
    private NewReleasesResource newReleasesResource;

    @Test
    void startNewReleasesProcess_successfulCall_returnsOk() {
        // GIVEN
        doNothing().when(newReleasesProcessService).startNewReleasesProcessByMessage();

        // WHEN
        ResponseEntity<Void> response = newReleasesResource.startNewReleasesProcess();

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(newReleasesProcessService, times(1)).startNewReleasesProcessByMessage();
    }

    @Test
    void startNewReleasesProcess_exceptionThrown_returnsInternalServerError() {
        // GIVEN
        doThrow(new RuntimeException("Something went wrong"))
                .when(newReleasesProcessService)
                .startNewReleasesProcessByMessage();

        // WHEN
        ResponseEntity<Void> response = newReleasesResource.startNewReleasesProcess();

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(newReleasesProcessService, times(1)).startNewReleasesProcessByMessage();
    }
}
