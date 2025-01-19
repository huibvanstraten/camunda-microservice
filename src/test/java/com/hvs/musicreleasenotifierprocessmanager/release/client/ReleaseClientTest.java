package com.hvs.musicreleasenotifierprocessmanager.release.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hvs.musicreleasenotifierprocessmanager.release.dto.ReleaseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReleaseClientTest {

    private ReleaseClient releaseClient;
    private HttpClient mockHttpClient;

    // Fake CORE_BASE_URL for tests.
    private final String coreBaseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        releaseClient = new ReleaseClient();

        ReflectionTestUtils.setField(releaseClient, "CORE_BASE_URL", coreBaseUrl);

        mockHttpClient = mock(HttpClient.class);
        ReflectionTestUtils.setField(releaseClient, "httpClient", mockHttpClient);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ReflectionTestUtils.setField(releaseClient, "objectMapper", objectMapper);
    }

    @Test
    void getAllReleasesForArtist_returnsAllReleases_acrossMultiplePages() throws Exception {
        String jsonPage1 = """
                {
                  "content": [
                    {
                      "releaseId": "r1",
                      "artistId": "artist123",
                      "totalTracks": 10,
                      "name": "Release One",
                      "releaseDate": "2024-05-24",
                      "type": "album",
                      "spotifyUrl": "https://open.spotify.com/album/abc"
                    }
                  ],
                  "total": 3,
                  "offset": 0,
                  "limit": 20,
                  "next": "/release?artistId=artist123&offset=20&limit=20",
                  "previous": null
                }""";
        String jsonPage2 = """
                {
                  "content": [
                    {
                      "releaseId": "r2",
                      "artistId": "artist123",
                      "totalTracks": 8,
                      "name": "Release Two",
                      "releaseDate": "2023-12-01",
                      "type": "single",
                      "spotifyUrl": "https://open.spotify.com/album/def"
                    }
                  ],
                  "total": 3,
                  "offset": 20,
                  "limit": 20,
                  "next": null,
                  "previous": "/release?artistId=artist123&offset=0&limit=20"
                }""";

        HttpResponse<String> fakeResponsePage1 = mock(HttpResponse.class);
        when(fakeResponsePage1.statusCode()).thenReturn(200);
        when(fakeResponsePage1.body()).thenReturn(jsonPage1);

        HttpResponse<String> fakeResponsePage2 = mock(HttpResponse.class);
        when(fakeResponsePage2.statusCode()).thenReturn(200);
        when(fakeResponsePage2.body()).thenReturn(jsonPage2);

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(fakeResponsePage1)
                .thenReturn(fakeResponsePage2);

        List<ReleaseDto> result = releaseClient.getAllReleasesForArtist("artist123");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getReleaseId()).isEqualTo("r1");
        assertThat(result.get(1).getReleaseId()).isEqualTo("r2");

        verify(mockHttpClient, times(2)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void getAllReleasesForArtist_throwsIOException_onNon200Response() throws Exception {
        HttpResponse<String> errorResponse = mock(HttpResponse.class);
        when(errorResponse.statusCode()).thenReturn(404);
        when(errorResponse.body()).thenReturn("Not Found");

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(errorResponse);

        assertThatThrownBy(() -> releaseClient.getAllReleasesForArtist("artist123"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Failed to fetch releases. Status Code: 404");

        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void sendNewRelease_callsHttpClientSend() throws Exception {
        // Given
        ReflectionTestUtils.setField(releaseClient, "CORE_BASE_URL", coreBaseUrl);

        HttpResponse<String> fakeResponse = mock(HttpResponse.class);
        when(fakeResponse.statusCode()).thenReturn(200);
        when(fakeResponse.body()).thenReturn("OK");

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(fakeResponse);

        releaseClient.sendNewRelease("r1");

        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}
