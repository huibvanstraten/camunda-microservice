package com.hvs.musicreleasenotifierprocessmanager.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hvs.musicreleasenotifierprocessmanager.client.response.UserPageResponse;
import com.hvs.musicreleasenotifierprocessmanager.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class MusicReleaseNotifierCoreClient {

    private static final Logger logger = LoggerFactory.getLogger(MusicReleaseNotifierCoreClient.class);
    private static final String CORE_BASE_URL = "http://localhost:8080/api/v1/user";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MusicReleaseNotifierCoreClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<UserDto> getUsersParametized(List<String> fields) throws IOException, InterruptedException {
        StringBuilder uriBuilder = new StringBuilder(String.format("%s/parameters?", CORE_BASE_URL));

        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            String encodedField = URLEncoder.encode(field, StandardCharsets.UTF_8);
            uriBuilder.append("fields=").append(encodedField);
            if (i < fields.size() - 1) {
                uriBuilder.append("&");
            }
        }

        String uri = uriBuilder.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .header("Accept", "application/json")
                .build();

        logger.info("Sending request to {}", uri);

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        logger.info("Received response with status code {}", response.statusCode());

        if (response.statusCode() == 200) {
            String responseBody = response.body();

            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } else {
            logger.error("Failed to fetch users. Status Code: {}, Body: {}", response.statusCode(), response.body());
            throw new IOException("Failed to fetch users. Status Code: " + response.statusCode());
        }
    }


    public UserPageResponse<UserDto> getUsersPaged(int page, int size) throws IOException, InterruptedException {
        String uri = String.format("%s/paginated?page=%d&size=%d", CORE_BASE_URL, page, size);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .header("Accept", "application/json")
                .build();

        logger.info("Sending request to {}", uri);

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        logger.info("Received response with status code {}", response.statusCode());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            return objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } else {
            logger.error("Failed to fetch users. Status Code: {}, Body: {}", response.statusCode(), response.body());
            throw new IOException("Failed to fetch users. Status Code: " + response.statusCode());
        }
    }
}
