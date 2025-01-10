package com.hvs.musicreleasenotifierprocessmanager.service;

import com.hvs.musicreleasenotifierprocessmanager.client.MusicReleaseNotifierCoreClient;
import com.hvs.musicreleasenotifierprocessmanager.client.response.UserPageResponse;
import com.hvs.musicreleasenotifierprocessmanager.dto.UserDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewReleasesProcessService {
    private static final Logger logger = LoggerFactory.getLogger(NewReleasesProcessService.class);

    private final MusicReleaseNotifierCoreClient musicReleaseNotifierCoreClient;
    private final RuntimeService runtimeService;

    @Autowired
    public NewReleasesProcessService(MusicReleaseNotifierCoreClient musicReleaseNotifierCoreClient, RuntimeService runtimeService) {
        this.musicReleaseNotifierCoreClient = musicReleaseNotifierCoreClient;
        this.runtimeService = runtimeService;
    }

    public void startNewReleasesProcessByMessage() {
        String messageName = "StartNewReleasesProcessMessage";
        runtimeService.startProcessInstanceByMessage(messageName);
    }

    public void print(DelegateExecution execution) {
        System.out.println("Blaaaaaaaaa");
    }

    public void getUsers(DelegateExecution execution) throws IOException, InterruptedException {
        List<String> fields = new ArrayList<String>();
        fields.add("username");
        fields.add("artistIdList");

        List<UserDto> users = musicReleaseNotifierCoreClient.getUsersParametized(fields);
        execution.setVariable("users", users);
    }

    public void getUsersPaged(DelegateExecution execution) throws IOException, InterruptedException {
        List<UserDto> allUsers = new ArrayList<>();
        int currentPage = 0;
        int size = 10;
        boolean hasMorePages = true;

        while (hasMorePages) {
            logger.info("Fetching page {} with size {}", currentPage, size);
            UserPageResponse<UserDto> pageResponse = musicReleaseNotifierCoreClient.getUsersPaged(currentPage, size);
            List<UserDto> users = pageResponse.getContent();
            allUsers.addAll(users);

            logger.info("Fetched {} users from page {}", users.size(), currentPage + 1);

            if (pageResponse.isLast()) {
                hasMorePages = false;
                logger.info("Reached the last page.");
            } else {
                currentPage++;
            }
        }

        execution.setVariable("allUsers", allUsers);
    }
}