package com.midpoint.demo.service;

import com.midpoint.demo.cli.client.MidPointClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private MidPointClient client;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        client = mock(MidPointClient.class);
        authService = new AuthService(client);
    }

    @Test
    void testAuthenticate() {
        authService.authenticate("user", "pass");
        verify(client).authenticate("user", "pass");
    }

    @Test
    void testTestAuthentication() {
        when(client.testAuthentication()).thenReturn(true);
        assertTrue(authService.testAuthentication());
        verify(client).testAuthentication();
    }
}
