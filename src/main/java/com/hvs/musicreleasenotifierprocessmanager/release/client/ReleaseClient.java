package com.hvs.musicreleasenotifierprocessmanager.release.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hvs.musicreleasenotifierprocessmanager.release.client.response.ReleasePagedResponse;
import com.hvs.musicreleasenotifierprocessmanager.release.dto.ReleaseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReleaseClient {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseClient.class);

    @Value("${core.base-url}")
    private String CORE_BASE_URL;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ReleaseClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }


    public List<ReleaseDto> getAllReleasesForArtist(String artistId) throws IOException, InterruptedException {
        List<ReleaseDto> allReleases = new ArrayList<>();
        int offset = 0;
        int limit = 20;

        String url = buildUrl(artistId, offset, limit);

        while (true) {
            logger.info("Fetching releases from: {}", url);
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                ReleasePagedResponse<ReleaseDto> page = objectMapper.readValue(
                        body, new TypeReference<>() {}
                );

                allReleases.addAll(page.getContent());

                if (page.getNext() == null) {
                    break;
                }

                url = CORE_BASE_URL + page.getNext();
            } else {
                logger.error("Failed to fetch releases. Status: {}, Body: {}",
                        response.statusCode(), response.body());
                throw new IOException("Failed to fetch releases. Status Code: " + response.statusCode());
            }
        }

        return allReleases;
    }

    public void sendNewRelease(String releaseId) {
        String newReleasesBaseUrl = CORE_BASE_URL + "/release/new";
        String uri = String.format("%s?releaseId=%s", newReleasesBaseUrl, releaseId);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .header("Accept", "application/json")
                .build();

        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        }


    private String buildUrl(String artistId, int offset, int limit) {
        String encodedArtist = URLEncoder.encode(artistId, StandardCharsets.UTF_8);
        return String.format("%s/release?artistId=%s&offset=%d&limit=%d",
                CORE_BASE_URL,
                encodedArtist,
                offset,
                limit
        );
    }
}
