package com.midpoint.demo.client;

import com.midpoint.demo.cli.client.MidPointClient;
import com.midpoint.demo.exception.MidPointException;
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

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient.Builder builder = WebClient.builder();
        midPointClient = new MidPointClient(builder, mockWebServer.url("/").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldAuthenticateSuccessfully() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        boolean result = midPointClient.authenticate("user", "pass");

        assertTrue(result);
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/users/search", request.getPath());
        assertEquals("POST", request.getMethod());
        assertNotNull(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void shouldThrowException_whenAuthenticationFails() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        assertThrows(MidPointException.class, () -> midPointClient.authenticate("user", "wrong"));
    }

}
