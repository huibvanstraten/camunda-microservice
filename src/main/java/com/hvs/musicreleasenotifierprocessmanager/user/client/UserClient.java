package com.hvs.musicreleasenotifierprocessmanager.user.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hvs.musicreleasenotifierprocessmanager.user.dto.UserDto;
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
import java.util.List;

@Component
public class UserClient {

    private static final Logger logger = LoggerFactory.getLogger(UserClient.class);

    @Value("${core.base-url}")
    private String CORE_BASE_URL = "";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UserClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<UserDto> getUsersParametized(List<String> fields) throws IOException, InterruptedException {
        StringBuilder uriBuilder = new StringBuilder(String.format("%s/parameters?", CORE_BASE_URL + "/user"));

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

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();

            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } else {
            logger.error("Failed to fetch users. Status Code: {}, Body: {}", response.statusCode(), response.body());
            throw new IOException("Failed to fetch users. Status Code: " + response.statusCode());
        }
    }
}
