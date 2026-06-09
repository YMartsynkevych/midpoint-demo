package com.midpoint.demo.client;

import com.midpoint.demo.model.MidpointResponse;
import com.midpoint.demo.model.ObjectModification;
import com.midpoint.demo.model.SearchQuery;
import com.midpoint.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class MidPointClient {

    private static final Logger logger = LoggerFactory.getLogger(MidPointClient.class);
    private final WebClient webClient;

    public MidPointClient(WebClient.Builder webClientBuilder,
                          @Value("${midpoint.base-url}") String baseUrl,
                          @Value("${MIDPOINT_USER:administrator}") String username,
                          @Value("${MIDPOINT_PASS:IGA4ever}") String password) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth(username, password))
                .build();
    }

    public List<User> searchUsers(String username) {

        logger.debug("Searching for user: {}", username);

        MidpointResponse response = webClient.post()
                .uri("/users/search?format=json") // important if needed
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(SearchQuery.byUsername(username))
                .retrieve()
                .bodyToMono(MidpointResponse.class)
                .block();

        if (response == null) {
            throw new IllegalStateException("Response from Midpoint is null");
        }
        return response.getObject().getObject();
    }

    public void updateUserEmail(String oid, String email) {
        logger.debug("Updating user {} email to {}", oid, email);

        Map<String, Object> body = Map.of(
                "objectModification", ObjectModification.replace("emailAddress", email)
        );

        webClient.patch()
                .uri("/users/{oid}", oid)
                .headers(h -> {
                    h.setContentType(MediaType.APPLICATION_JSON);
                    h.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void updateUser(String oid, Map<String, Object> updates) {
        logger.debug("Updating user {} with fields: {}", oid, updates);

        Map<String, Object> body = Map.of(
                "objectModification", ObjectModification.replace(updates)
        );

        webClient.patch()
                .uri("/users/{oid}", oid)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
