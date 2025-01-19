package com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.service;

import com.hvs.musicreleasenotifierprocessmanager.artist.client.ArtistClient;
import com.hvs.musicreleasenotifierprocessmanager.artist.dto.ArtistDto;
import com.hvs.musicreleasenotifierprocessmanager.user.dto.UserDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings("unused")
public class GetArtistsForUserProcessService {

    private static final Logger logger = LoggerFactory.getLogger(GetArtistsForUserProcessService.class);

    private final ArtistClient artistClient;
    private final RuntimeService runtimeService;

    @Autowired
    public GetArtistsForUserProcessService(ArtistClient artistClient, RuntimeService runtimeService) {
        this.artistClient = artistClient;
        this.runtimeService = runtimeService;
    }

    public void setLists(DelegateExecution execution) {
        UserDto user = (UserDto) execution.getVariable("user");
        List<String> artistIdList = user.getArtistIdList();

        execution.setVariable("artistIdList", artistIdList);
        execution.setVariable("artistDataList", new ArrayList<ArtistDto>());
    }

    @SuppressWarnings({"unchecked"})
    public void getArtist(DelegateExecution execution) {
        String artistId = (String) execution.getVariable("artistId");

        try {
            ArtistDto artistData = artistClient.getArtist(artistId);

            List<ArtistDto> artistDataList = (List<ArtistDto>) execution.getVariable("artistDataList");
            artistDataList.add(artistData);
            execution.setVariable("artistDataList", artistDataList);
        } catch (Exception e) {
            throw new BpmnError("FetchArtistDataError", e);
        }
    }

    public void sendErrorMessage(DelegateExecution execution, String message) {
        runtimeService.createMessageCorrelation(message)
                .processInstanceId(execution.getProcessInstanceId())
                .setVariable("artistId", execution.getVariable("artistId"))
                .correlate();
    }
}
