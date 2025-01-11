package com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.web.rest;

import com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.service.NewReleasesProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/release")
public class NewReleasesResource {
    private final NewReleasesProcessService newReleasesProcessService;

    private static final Logger logger = LoggerFactory.getLogger(NewReleasesResource.class);


    public NewReleasesResource(NewReleasesProcessService newReleasesProcessService) {
        this.newReleasesProcessService = newReleasesProcessService;
    }

    @PostMapping("/start")
    public ResponseEntity<Void> startNewReleasesProcess() {
        try {
            newReleasesProcessService.startNewReleasesProcessByMessage();
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Starting of new releases process failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }


}
