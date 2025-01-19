package com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.service;

import com.hvs.musicreleasenotifierprocessmanager.artist.dto.ArtistDto;
import com.hvs.musicreleasenotifierprocessmanager.release.client.ReleaseClient;
import com.hvs.musicreleasenotifierprocessmanager.release.dto.ReleaseDto;
import com.hvs.musicreleasenotifierprocessmanager.user.client.UserClient;
import com.hvs.musicreleasenotifierprocessmanager.user.dto.UserDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings({"unused", "unchecked"})
public class NewReleasesProcessService {
    private static final Logger logger = LoggerFactory.getLogger(NewReleasesProcessService.class);

    private final UserClient userClient;
    private final ReleaseClient releaseClient;
    private final RuntimeService runtimeService;

    @Autowired
    public NewReleasesProcessService(UserClient userClient, RuntimeService runtimeService, ReleaseClient releaseClient) {
        this.userClient = userClient;
        this.runtimeService = runtimeService;
        this.releaseClient = releaseClient;
    }

    public void startNewReleasesProcessByMessage() {
        String messageName = "StartNewReleasesProcessMessage";
        runtimeService.startProcessInstanceByMessage(messageName);
    }

    public void getUsers(DelegateExecution execution) throws IOException, InterruptedException {
        List<String> fields = new ArrayList<>();
        fields.add("username");
        fields.add("artistIdList");

        List<UserDto> users = userClient.getUsersParametized(fields);
        execution.setVariable("users", users);
    }

    @SuppressWarnings("unchecked")
    public boolean hasArtists(DelegateExecution execution) {
        List<ArtistDto> artistDataList = (List<ArtistDto>) execution.getVariable("artistDataList");
        return !artistDataList.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public boolean hasReleases(DelegateExecution execution) {
        List<ReleaseDto> releaseDataList = (List<ReleaseDto>) execution.getVariable("releaseDataList");
        return !releaseDataList.isEmpty();
    }

    public void logFetchingErrors(DelegateExecution execution) {
        logger.error("Error fetching data from {}, for user {}", execution.getVariable("artistId"), execution.getVariable("username"));
    }

    public void filterReleasesOnDate(DelegateExecution execution) {
        List<ReleaseDto> releases = (List<ReleaseDto>) execution.getVariable("releaseDataList");

        List<String> filtered = releases.stream()
                .filter(release -> release.getReleaseDate().isAfter(LocalDate.now().minusYears(1)))
                .map(ReleaseDto::getReleaseId)
                .toList();

        execution.setVariable("newReleases", filtered);
    }

    public void sendNewReleases(DelegateExecution execution) {
        List<String> releases = (List<String>) execution.getVariable("newReleases");

        releases.forEach(releaseClient::sendNewRelease);
    }
}