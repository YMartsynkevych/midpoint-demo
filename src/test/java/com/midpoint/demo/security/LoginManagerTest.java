package com.midpoint.demo.security;

import com.midpoint.demo.cli.client.MidPointClient;
import com.midpoint.demo.exception.MidPointException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginManagerTest {

    @Mock
    private MidPointClient midPointClient;

    @InjectMocks
    private LoginManager loginManager;

    @Test
    void shouldLoginSuccessfully_onFirstAttempt() {
        String input = "user\npass\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(midPointClient.authenticate("user", "pass")).thenReturn(true);

        boolean result = loginManager.login(scanner);

        assertTrue(result);
        verify(midPointClient, times(1)).authenticate("user", "pass");
    }

    @Test
    void shouldLoginSuccessfully_onThirdAttempt() {
        String input = "user\nwrong1\nuser\nwrong2\nuser\ncorrect\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(midPointClient.authenticate("user", "wrong1")).thenReturn(false);
        when(midPointClient.authenticate("user", "wrong2")).thenReturn(false);
        when(midPointClient.authenticate("user", "correct")).thenReturn(true);

        boolean result = loginManager.login(scanner);

        assertTrue(result);
        verify(midPointClient, times(3)).authenticate(anyString(), anyString());
    }

    @Test
    void shouldFailLogin_afterThreeAttempts() {
        String input = "user\nwrong1\nuser\nwrong2\nuser\nwrong3\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(midPointClient.authenticate(anyString(), anyString())).thenReturn(false);

        boolean result = loginManager.login(scanner);

        assertFalse(result);
        verify(midPointClient, times(3)).authenticate(anyString(), anyString());
    }

    @Test
    void shouldHandleExceptionDuringAuthentication() {
        String input = "user\npass\nuser\npass\nuser\npass\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(midPointClient.authenticate(anyString(), anyString())).thenThrow(new MidPointException("Connection error"));

        boolean result = loginManager.login(scanner);

        assertFalse(result);
        verify(midPointClient, times(3)).authenticate(anyString(), anyString());
    }

    @Test
    void shouldLogoutSuccessfully() {
        loginManager.logout();

        verify(midPointClient).logout();
    }
}
