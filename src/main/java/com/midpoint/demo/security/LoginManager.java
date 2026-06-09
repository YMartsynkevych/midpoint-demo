package com.midpoint.demo.security;

import com.midpoint.demo.exception.MidPointAuthenticationException;
import com.midpoint.demo.exception.MidPointException;
import com.midpoint.demo.service.AuthService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class LoginManager {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final AuthService authService;

    public LoginManager(AuthService authService) {
        this.authService = authService;
    }

    public boolean login(Scanner scanner) {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            Credentials credentials = readCredentials(scanner);

            if (authenticate(credentials)) {
                System.out.println("Login successful");
                return true;
            }

            if (attempt < MAX_LOGIN_ATTEMPTS) {
                System.out.println("Retry " + attempt + "/" + MAX_LOGIN_ATTEMPTS);
            }
        }
        System.out.println("Too many failed attempts. Terminating.");
        return false;
    }

    private Credentials readCredentials(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password;
        if (System.console() != null) {
            char[] passwordChars = System.console().readPassword();
            password = new String(passwordChars);
        } else {
            password = scanner.nextLine();
        }
        return new Credentials(username, password);
    }

    private boolean authenticate(Credentials credentials) {
        try {
            authService.authenticate(credentials.username(), credentials.password());
            if (authService.testAuthentication()) {
                return true;
            }
            System.out.println("Invalid credentials");
        } catch (MidPointAuthenticationException e) {
            System.out.println("Invalid credentials");
        } catch (MidPointException e) {
            System.out.println("Authentication error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
        return false;
    }

    public void logout() {
        System.out.println("Logging out...");
        authService.logout();
    }

    public record Credentials(String username, String password) {}
}
