package com.midpoint.demo.security;

import com.midpoint.demo.exception.MidPointAuthenticationException;
import com.midpoint.demo.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginManagerTest {

    private AuthService authService;
    private LoginManager loginManager;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        loginManager = new LoginManager(authService);
    }

    @Test
    void testLoginSuccess() {
        Scanner scanner = new Scanner("user\npass\n");
        when(authService.testAuthentication()).thenReturn(true);

        boolean result = loginManager.login(scanner);

        assertTrue(result);
        verify(authService).authenticate("user", "pass");
    }

    @Test
    void testLoginRetrySuccess() {
        Scanner scanner = new Scanner("user\nwrong\nuser\npass\n");
        
        when(authService.testAuthentication())
                .thenReturn(false)
                .thenReturn(true);

        boolean result = loginManager.login(scanner);

        assertTrue(result);
        verify(authService, times(2)).authenticate(anyString(), anyString());
    }

    @Test
    void testLoginFailureMaxAttempts() {
        Scanner scanner = new Scanner("user\nwrong\nuser\nwrong\nuser\nwrong\n");
        
        when(authService.testAuthentication()).thenReturn(false);

        boolean result = loginManager.login(scanner);

        assertFalse(result);
        verify(authService, times(3)).authenticate(anyString(), anyString());
    }

    @Test
    void testLoginAuthenticationException() {
        Scanner scanner = new Scanner("user\npass\nuser\npass\nuser\npass\n");
        doThrow(new MidPointAuthenticationException("Invalid"))
                .when(authService).authenticate(anyString(), anyString());

        boolean result = loginManager.login(scanner);

        assertFalse(result);
        verify(authService, times(3)).authenticate(anyString(), anyString());
    }

    @Test
    void testLogout() {
        loginManager.logout();
        verify(authService).logout();
    }
}
