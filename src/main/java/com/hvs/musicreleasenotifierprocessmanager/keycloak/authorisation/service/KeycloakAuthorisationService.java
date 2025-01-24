package com.hvs.musicreleasenotifierprocessmanager.keycloak.authorisation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hvs.musicreleasenotifierprocessmanager.keycloak.authorisation.response.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class KeycloakAuthorisationService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakAuthorisationService.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public KeycloakAuthorisationService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Value("${keycloak.token-endpoint}")
    private String url;

    @Value("${keycloak.client-id}")
    private String clientId = "";

    @Value("${keycloak.client-secret}")
    private String clientSecret = "";

    @Value("${keycloak.grant-type}")
    private String grantType = "";

    public TokenResponse getToken() throws IOException, InterruptedException {

        String formData = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                + "&grant_type=" + URLEncoder.encode(grantType, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();

            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } else {
            logger.error("Failed to fetch token. Status Code: {}, Body: {}", response.statusCode(), response.body());
            throw new IllegalStateException("Failed to fetch token. Status Code: " + response.statusCode());
        }
    }
}
