package com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.service;

import com.hvs.musicreleasenotifierprocessmanager.artist.client.ArtistClient;
import com.hvs.musicreleasenotifierprocessmanager.artist.dto.ArtistDto;
import com.hvs.musicreleasenotifierprocessmanager.user.dto.UserDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.camunda.bpm.extension.mockito.CamundaMockito.delegateExecutionFake;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class GetArtistsForUserProcessServiceTest {

    @Mock
    private ArtistClient artistClient;

    @Mock
    private RuntimeService runtimeService;

    @InjectMocks
    private GetArtistsForUserProcessService getArtistsForUserProcessService;

    private DelegateExecution delegateExecution;

    @BeforeEach
    void setUp() {
        delegateExecution = delegateExecution();
    }

    @Test
    void testSetLists() {
        // WHEN
        getArtistsForUserProcessService.setLists(delegateExecution);

        // THEN
        List<String> artistIdList = (List<String>)delegateExecution.getVariable("artistIdList");
        assertThat(artistIdList).isNotNull();
        assertThat(artistIdList.size()).isEqualTo(2);
        assertThat(artistIdList.getFirst()).isEqualTo("id1");

        List<ArtistDto> artistDataList = (List<ArtistDto>) delegateExecution.getVariable("artistDataList");
        assertThat(artistDataList).isEmpty();
    }

    @Test
    void testGetArtist() throws IOException, InterruptedException {
        // GIVEN
        delegateExecution.setVariable("artistId", "id1");
        delegateExecution.setVariable("artistDataList", new ArrayList<ArtistDto>());

        when(artistClient.getArtist(any())).thenReturn(artist());

        // WHEN
        getArtistsForUserProcessService.getArtist(delegateExecution);

        // THEN
        List<ArtistDto> artistDtoList = (List<ArtistDto>) delegateExecution.getVariable("artistDataList");

        assertThat(artistDtoList).isNotNull();
        assertThat(artistDtoList.size()).isEqualTo(1);
        assertThat(artistDtoList.getFirst().getName()).isEqualTo("testArtist");
    }

    @Test
    void testGetArtist_throwsBpmnError() throws IOException, InterruptedException {
        // GIVEN
        delegateExecution.setVariable("artistId", "id1");
        delegateExecution.setVariable("artistDataList", new ArrayList<ArtistDto>());

        // Simulate an error from the client
        when(artistClient.getArtist(any())).thenThrow(new RuntimeException("Client error"));

        // WHEN / THEN
        assertThatThrownBy(() -> getArtistsForUserProcessService.getArtist(delegateExecution))
                .isInstanceOf(BpmnError.class)
                .hasFieldOrPropertyWithValue("errorCode", "FetchArtistDataError");
    }

    private DelegateExecution delegateExecution() {
        return delegateExecutionFake()
                .withBusinessKey("businessKey")
                .withProcessInstanceId("processInstanceId")
                .withVariables(
                        Map.of(
                                "user", user()
                        )
                );
    }

    private UserDto user() {
        return new UserDto(
                "TestUser",
                List.of("id1","id2")
        );
    }

    private ArtistDto artist() {
        return new ArtistDto(
                "testArtist",
                "id1"
        );
    }
}
