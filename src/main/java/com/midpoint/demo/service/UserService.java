package com.midpoint.demo.service;

import com.midpoint.demo.client.MidPointClient;
import com.midpoint.demo.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final MidPointClient client;
    private final Map<String, String> usernameToOid = new ConcurrentHashMap<>();

    public UserService(MidPointClient client) {
        this.client = client;
    }
    
    @PostConstruct
    public void initCache() {
        logger.info("Initializing user OID cache...");
        try {
            List<User> users = client.searchUsers(null);
            if (users != null) {
                users.forEach(user -> {
                    if (user.getName() != null && user.getOid() != null) {
                        usernameToOid.put(user.getName(), user.getOid());
                    }
                });
                logger.info("Loaded {} users into cache", usernameToOid.size());
            }
        } catch (Exception e) {
            logger.error("Failed to initialize user cache: {}", e.getMessage());
        }
    }

    public List<User> searchByUsername(String username) {
        return client.searchUsers(username);
    }

    private String resolveOid(String username) {
        if (usernameToOid.containsKey(username)) {
            logger.debug("Resolved OID for username {} from cache: {}", username, usernameToOid.get(username));
            return usernameToOid.get(username);
        }

        logger.debug("Username {} not found in cache, searching MidPoint...", username);
        List<User> users = searchByUsername(username);
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }

        // MidPoint 'name' is the username
        User user = users.get(0);
        logger.debug("Resolved OID for username {} after search: {}", username, user.getOid());
        return user.getOid();
    }

    public void updateUserByUsername(String username, String email, String givenName, String familyName, String newUsername) {
        String oid = resolveOid(username);
        updateUser(oid, email, givenName, familyName, newUsername);

        if (newUsername != null && !newUsername.equals(username)) {
            usernameToOid.remove(username);
            usernameToOid.put(newUsername, oid);
            logger.debug("Updated username in cache from {} to {}", username, newUsername);
        }
    }

    public void updateUser(String oid, String email, String givenName, String familyName, String newUsername) {
        Map<String, Object> updates = new HashMap<>();
        if (email != null) updates.put("emailAddress", email);
        if (givenName != null) updates.put("givenName", givenName);
        if (familyName != null) updates.put("familyName", familyName);
        if (newUsername != null) updates.put("name", newUsername);

        if (updates.isEmpty()) {
            throw new IllegalArgumentException("No fields provided for update");
        }

        client.updateUser(oid, updates);
    }
}
