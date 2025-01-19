package com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.service;

import com.hvs.musicreleasenotifierprocessmanager.artist.dto.ArtistDto;
import com.hvs.musicreleasenotifierprocessmanager.release.client.ReleaseClient;
import com.hvs.musicreleasenotifierprocessmanager.release.dto.ReleaseDto;
import com.hvs.musicreleasenotifierprocessmanager.user.client.UserClient;
import com.hvs.musicreleasenotifierprocessmanager.user.dto.UserDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.extension.mockito.CamundaMockito.delegateExecutionFake;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked"})
class NewReleasesProcessServiceTest {

    @Mock
    private UserClient userClient;

    @Mock
    private ReleaseClient releaseClient;

    @Mock
    private RuntimeService runtimeService;

    @InjectMocks
    private NewReleasesProcessService newReleasesProcessService;

    private DelegateExecution delegateExecution;

    @BeforeEach
    void setUp() {
        delegateExecution = delegateExecutionFake()
                .withProcessInstanceId("processInstanceId");
    }

    @Test
    void testStartNewReleasesProcessByMessage() {
        // Given
        String messageName = "StartNewReleasesProcessMessage";

        // When
        newReleasesProcessService.startNewReleasesProcessByMessage();

        // Then
        verify(runtimeService).startProcessInstanceByMessage(messageName);
    }

    @Test
    void testGetUsers() throws IOException, InterruptedException {
        // Given
        List<UserDto> mockUsers = List.of(
                new UserDto("TestUser1", List.of("artist1")),
                new UserDto("TestUser2", List.of("artist2", "artist3"))
        );
        when(userClient.getUsersParametized(any())).thenReturn(mockUsers);

        // When
        newReleasesProcessService.getUsers(delegateExecution);

        // Then
        List<UserDto> actualUsers = (List<UserDto>) delegateExecution.getVariable("users");
        assertThat(actualUsers).hasSize(2);
        assertThat(actualUsers.get(0).getUsername()).isEqualTo("TestUser1");
        assertThat(actualUsers.get(1).getUsername()).isEqualTo("TestUser2");
    }

    @Test
    void testHasArtists_Empty() {
        // Given
        delegateExecution.setVariable("artistDataList", new ArrayList<ArtistDto>());

        // When
        boolean result = newReleasesProcessService.hasArtists(delegateExecution);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testHasArtists_NonEmpty() {
        // Given
        List<ArtistDto> artistDataList = new ArrayList<>();
        artistDataList.add(new ArtistDto("TestArtist", "id1"));
        delegateExecution.setVariable("artistDataList", artistDataList);

        // When
        boolean result = newReleasesProcessService.hasArtists(delegateExecution);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testHasReleases_Empty() {
        // Given
        delegateExecution.setVariable("releaseDataList", new ArrayList<ReleaseDto>());

        // When
        boolean result = newReleasesProcessService.hasReleases(delegateExecution);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testHasReleases_NonEmpty() {
        // Given
        List<ReleaseDto> releases = new ArrayList<>();
        releases.add(new ReleaseDto("releaseId", "artistId", 10, "Name", LocalDate.now(), "type", "url"));
        delegateExecution.setVariable("releaseDataList", releases);

        // When
        boolean result = newReleasesProcessService.hasReleases(delegateExecution);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testFilterReleasesOnDate() {
        // Given
        List<ReleaseDto> releases = new ArrayList<>();
        releases.add(new ReleaseDto("r1", "artist", 10, "Old Release", LocalDate.now().minusYears(2), "type", "url"));
        releases.add(new ReleaseDto("r2", "artist", 5, "Recent Release", LocalDate.now().minusMonths(6), "type", "url"));
        releases.add(new ReleaseDto("r3", "artist", 5, "Release from 1 day ago", LocalDate.now().minusDays(1), "type", "url"));
        delegateExecution.setVariable("releaseDataList", releases);

        // When
        newReleasesProcessService.filterReleasesOnDate(delegateExecution);

        // Then
        List<String> newReleases = (List<String>) delegateExecution.getVariable("newReleases");
        assertThat(newReleases).hasSize(2);
        assertThat(newReleases).contains("r2", "r3");
    }

    @Test
    void testSendNewReleases() {
        // Given
        List<String> releaseIds = List.of("r1", "r2", "r3");
        delegateExecution.setVariable("newReleases", releaseIds);

        // When
        newReleasesProcessService.sendNewReleases(delegateExecution);

        // Then
        verify(releaseClient, times(3)).sendNewRelease(anyString());
        verify(releaseClient).sendNewRelease("r1");
        verify(releaseClient).sendNewRelease("r2");
        verify(releaseClient).sendNewRelease("r3");
    }
}
