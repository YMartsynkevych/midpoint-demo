package com.midpoint.demo.cli.client;

import com.midpoint.demo.api.dto.request.ObjectModification;
import com.midpoint.demo.api.dto.request.SearchQuery;
import com.midpoint.demo.api.dto.response.MidpointResponse;
import com.midpoint.demo.domain.User;
import com.midpoint.demo.exception.MidPointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class MidPointClient {

    private static final String USERS_SEARCH_ENDPOINT = "/users/search";
    private static final String USERS_SEARCH_JSON_ENDPOINT = "/users/search?format=json";
    private static final String USER_OID_ENDPOINT = "/users/{oid}";

    private static final Logger logger = LoggerFactory.getLogger(MidPointClient.class);
    private final WebClient.Builder webClientBuilder;
    private final String baseUrl;
    private WebClient webClient;

    public MidPointClient(WebClient.Builder webClientBuilder,
                          @Value("${midpoint.base-url}") String baseUrl) {
        this.webClientBuilder = webClientBuilder;
        this.baseUrl = baseUrl;
    }
    public boolean authenticate(String username, String password) {
         webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth(username, password))
                .build();

        try {
            webClient.post()
                    .uri(USERS_SEARCH_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(SearchQuery.byUsername(null))
                    .retrieve()
                    .onStatus(status -> status.equals(HttpStatus.UNAUTHORIZED),
                            response -> Mono.error(new MidPointException("Invalid credentials")))
                    .toBodilessEntity()
                    .block();
            logger.info("Successfully authenticated");

        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            throw new MidPointException("Failed to connect to MidPoint: " + e.getMessage(), e);
        }
        return true;
    }

    public void logout() {
        logger.info("Closing MidPoint client and clearing credentials...");
        this.webClient = null;
    }

    public List<User> searchUsers(String username) {
        logger.debug("Searching for user: {}", username);

        try {
            MidpointResponse response = webClient.post()
                    .uri(USERS_SEARCH_JSON_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(SearchQuery.byUsername(username))
                    .retrieve()
                    .onStatus(status -> status.equals(HttpStatus.UNAUTHORIZED),
                            res -> Mono.error(new MidPointException("Unauthorized access")))
                    .bodyToMono(MidpointResponse.class)
                    .block();

            if (response == null || response.getObject() == null) {
                return List.of();
            }
            return response.getObject().getObject();
        } catch (Exception e) {
            throw new MidPointException("Error during user search: " + e.getMessage(), e);
        }
    }

    public void updateUser(String oid, Map<String, Object> updates) {
        logger.debug("Updating user {} with fields: {}", oid, updates);
        Map<String, Object> body = Map.of(
                "objectModification", ObjectModification.replace(updates)
        );

        try {
            webClient.patch()
                    .uri(USER_OID_ENDPOINT, oid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(status -> status.equals(HttpStatus.UNAUTHORIZED),
                            res -> Mono.error(new MidPointException("Unauthorized access")))
                    .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                            res -> Mono.error(new MidPointException("User with OID " + oid + " not found")))
                    .toBodilessEntity()
                    .block();
        } catch (MidPointException e) {
            throw e;
        } catch (Exception e) {
            throw new MidPointException("Error updating user: " + e.getMessage(), e);
        }
    }
}
