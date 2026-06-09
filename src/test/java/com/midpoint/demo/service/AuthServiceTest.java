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
        when(client.authenticate("user", "pass")).thenReturn(true);
        boolean result = authService.authenticate("user", "pass");
        assertTrue(result);
        verify(client).authenticate("user", "pass");
    }

    @Test
    void testLogout() {
        authService.logout();
        verify(client).logout();
    }
}
