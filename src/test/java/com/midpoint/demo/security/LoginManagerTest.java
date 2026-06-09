package com.midpoint.demo.security;

import com.midpoint.demo.cli.MidPointCommand;
import com.midpoint.demo.client.MidPointClient;
import com.midpoint.demo.exception.MidPointAuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginManagerTest {

    private MidPointCommand midPointCommand;
    private MidPointClient midPointClient;
    private LoginManager loginManager;

    @BeforeEach
    void setUp() {
        midPointCommand = mock(MidPointCommand.class);
        midPointClient = mock(MidPointClient.class);
        when(midPointCommand.getMidPointClient()).thenReturn(midPointClient);
        loginManager = new LoginManager(midPointCommand);
    }

    @Test
    void testLoginSuccess() {
        Scanner scanner = new Scanner("user\npass\n");
        when(midPointClient.testAuthentication()).thenReturn(true);

        boolean result = loginManager.login(scanner);

        assertTrue(result);
        verify(midPointClient).authenticate("user", "pass");
    }

    @Test
    void testLoginRetrySuccess() {
        Scanner scanner = new Scanner("user\nwrong\nuser\npass\n");
        
        when(midPointClient.testAuthentication())
                .thenReturn(false)
                .thenReturn(true);

        boolean result = loginManager.login(scanner);

        assertTrue(result);
        verify(midPointClient, times(2)).authenticate(anyString(), anyString());
    }

    @Test
    void testLoginFailureMaxAttempts() {
        Scanner scanner = new Scanner("user\nwrong\nuser\nwrong\nuser\nwrong\n");
        
        when(midPointClient.testAuthentication()).thenReturn(false);

        boolean result = loginManager.login(scanner);

        assertFalse(result);
        verify(midPointClient, times(3)).authenticate(anyString(), anyString());
    }

    @Test
    void testLoginAuthenticationException() {
        Scanner scanner = new Scanner("user\npass\nuser\npass\nuser\npass\n");
        doThrow(new MidPointAuthenticationException("Invalid"))
                .when(midPointClient).authenticate(anyString(), anyString());

        boolean result = loginManager.login(scanner);

        assertFalse(result);
        verify(midPointClient, times(3)).authenticate(anyString(), anyString());
    }
}
