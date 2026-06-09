package com.midpoint.demo.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midpoint.demo.exception.MidPointAuthenticationException;
import com.midpoint.demo.exception.MidPointException;
import com.midpoint.demo.model.SearchQuery;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MidPointClientTest {

    private MockWebServer mockWebServer;
    private MidPointClient midPointClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        midPointClient = new MidPointClient(WebClient.builder(), baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testAuthenticationSuccess() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{}")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        midPointClient.authenticate("user", "pass");
        boolean result = midPointClient.testAuthentication();

        assertTrue(result);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/users/search", recordedRequest.getPath());
        assertNotNull(recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
        assertTrue(recordedRequest.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Basic "));
    }

    @Test
    void testAuthenticationFailure() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("Unauthorized")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        midPointClient.authenticate("user", "wrong-pass");
        
        assertThrows(MidPointAuthenticationException.class, () -> midPointClient.testAuthentication());
    }

    @Test
    void testSearchUsers_NotAuthenticated() {
        assertThrows(MidPointAuthenticationException.class, () -> midPointClient.searchUsers("test"));
    }

    @Test
    void testSearchUsers() throws Exception {
        String jsonResponse = "{\"object\": {\"object\": [{\"oid\": \"123\", \"name\": \"testuser\"}]}}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        midPointClient.authenticate("user", "pass");
        var users = midPointClient.searchUsers("testuser");

        assertEquals(1, users.size());
        assertEquals("123", users.get(0).getOid());
        assertEquals("testuser", users.get(0).getName());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/users/search?format=json", recordedRequest.getPath());
        
        SearchQuery sentQuery = objectMapper.readValue(recordedRequest.getBody().readUtf8(), SearchQuery.class);
        assertNotNull(sentQuery.getQuery().getFilter().getText());
        assertTrue(sentQuery.getQuery().getFilter().getText().contains("testuser"));
    }

    @Test
    void testUpdateUser() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        midPointClient.authenticate("user", "pass");
        midPointClient.updateUser("123", java.util.Map.of("emailAddress", "new@example.com"));

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("PATCH", recordedRequest.getMethod());
        assertEquals("/users/123", recordedRequest.getPath());
        assertTrue(recordedRequest.getBody().readUtf8().contains("new@example.com"));
    }
}
