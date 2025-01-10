package com.hvs.musicreleasenotifierprocessmanager.artist.service;

import org.camunda.bpm.engine.runtime.Execution;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {

    public void getArtistsForUser(Execution execution) {
        System.out.println("Executing getArtistsForUser");
    }
}
