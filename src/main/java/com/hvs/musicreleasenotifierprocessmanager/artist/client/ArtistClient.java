package com.hvs.musicreleasenotifierprocessmanager.artist.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hvs.musicreleasenotifierprocessmanager.artist.dto.ArtistDto;
import com.hvs.musicreleasenotifierprocessmanager.keycloak.authorisation.service.KeycloakAuthorisationService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ArtistClient {

    private static final Logger logger = LoggerFactory.getLogger(ArtistClient.class);

    @Value("${core.base-url}")
    private String CORE_BASE_URL = "";

    private final KeycloakAuthorisationService keycloakAuthorisationService;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ArtistClient(KeycloakAuthorisationService keycloakAuthorisationService) {
        this.keycloakAuthorisationService = keycloakAuthorisationService;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public ArtistDto getArtist(String artistId) throws IOException, InterruptedException {
        String token = keycloakAuthorisationService.getToken().getAccessToken();

        String artistBaseUrl = CORE_BASE_URL + "/artist";
        String uri = String.format("%s?artistId=%s", artistBaseUrl, artistId);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();

            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } else {
            logger.error("Failed to fetch artist. Status Code: {}, Body: {}", response.statusCode(), response.body());
            throw new BpmnError("FetchArtistDataError", "Failed to fetch artist");
        }
    }
}
