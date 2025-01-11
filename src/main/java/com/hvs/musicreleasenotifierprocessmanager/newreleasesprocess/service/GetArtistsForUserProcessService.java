package com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.service;

import com.hvs.musicreleasenotifierprocessmanager.artist.client.ArtistClient;
import com.hvs.musicreleasenotifierprocessmanager.artist.dto.ArtistDto;
import com.hvs.musicreleasenotifierprocessmanager.user.dto.UserDto;
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
@SuppressWarnings("unused")
public class GetArtistsForUserProcessService {

    private static final Logger logger = LoggerFactory.getLogger(GetArtistsForUserProcessService.class);

    private final ArtistClient artistClient;

    @Autowired
    public GetArtistsForUserProcessService(ArtistClient artistClient, RuntimeService runtimeService) {
        this.artistClient = artistClient;
    }

    public void setLists(DelegateExecution execution) {
        UserDto user = (UserDto) execution.getVariable("user");
        List<String> artistIdList = user.getArtistIdList();

        execution.setVariable("artistIdList", artistIdList);
        execution.setVariable("artistDataList", new ArrayList<ArtistDto>());
    }

    @SuppressWarnings({"unchecked"})
    public void getArtist(DelegateExecution execution) throws IOException, InterruptedException {
        String artistId = (String) execution.getVariable("artistId");
        ArtistDto artistData = artistClient.getArtist(artistId);

        List<ArtistDto> artistDataList = (List<ArtistDto>) execution.getVariable("artistDataList");
        artistDataList.add(artistData);
        execution.setVariable("artistDataList", artistDataList);

        for(ArtistDto artist : artistDataList) {
            System.out.println(artist.getName());
        }
    }
}
