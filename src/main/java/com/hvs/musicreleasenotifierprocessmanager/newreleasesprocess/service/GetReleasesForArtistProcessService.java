package com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.service;

import com.hvs.musicreleasenotifierprocessmanager.artist.dto.ArtistDto;
import com.hvs.musicreleasenotifierprocessmanager.release.client.ReleaseClient;
import com.hvs.musicreleasenotifierprocessmanager.release.dto.ReleaseDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings({"unused", "unchecked"})
public class GetReleasesForArtistProcessService {

    private final ReleaseClient releaseClient;
    private final RuntimeService runtimeService;

    @Autowired
    public GetReleasesForArtistProcessService(ReleaseClient releaseClient, RuntimeService runtimeService) {
        this.releaseClient = releaseClient;
        this.runtimeService = runtimeService;
    }

    public void getReleases(DelegateExecution execution) {
        List<ArtistDto> artistDataList = (List<ArtistDto>) execution.getVariable("artistDataList");
        try {
            List<ReleaseDto> releases = new ArrayList<>();
            for (ArtistDto artistDto : artistDataList) {
                execution.setVariable("artistId", artistDto.getArtistId());
                List<ReleaseDto> releasesForArtist = releaseClient.getAllReleasesForArtist(artistDto.getArtistId());
                releases.addAll(releasesForArtist);
            }
            execution.setVariable("releaseDataList", releases);
        } catch (Exception e) {
            throw new BpmnError("FetchReleasesError", e);
        }
    }

    public void sendErrorMessage(DelegateExecution execution, String messageName) {
        DelegateExecution parentExecution = execution.getSuperExecution();
        if (parentExecution == null) {
            throw new IllegalStateException("No parent process found!");
        }

        String parentProcessInstanceId = parentExecution.getProcessInstanceId();

        runtimeService
                .createMessageCorrelation(messageName)
                .processInstanceId(parentProcessInstanceId)
                .setVariable("artistId", execution.getVariable("artistId"))
                .correlate();
    }

}
