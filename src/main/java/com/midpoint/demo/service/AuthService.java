package com.midpoint.demo.service;

import com.midpoint.demo.cli.client.MidPointClient;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final MidPointClient client;

    public AuthService(MidPointClient client) {
        this.client = client;
    }

    public boolean authenticate(String username, String password) {
        return client.authenticate(username, password);
    }

    public void logout() {
        client.logout();
    }
}
