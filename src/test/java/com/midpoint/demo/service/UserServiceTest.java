package com.midpoint.demo.service;

import com.midpoint.demo.cli.client.MidPointClient;
import com.midpoint.demo.domain.User;
import com.midpoint.demo.exception.MidPointNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private MidPointClient client;
    private UserService userService;

    @BeforeEach
    void setUp() {
        client = mock(MidPointClient.class);
        userService = new UserService(client);
    }

    @Test
    void testSearchByUsername() {
        User user = User.builder().oid("1").name("user1").build();
        when(client.searchUsers("user1")).thenReturn(List.of(user));

        List<User> result = userService.searchByUsername("user1");

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getName());
        verify(client).searchUsers("user1");
    }

    @Test
    void testUpdateUserByUsername_WithAllFields() {
        User user = User.builder().oid("oid123").name("user1").build();
        when(client.searchUsers("user1")).thenReturn(List.of(user));

        userService.updateUserByUsername("user1", "new@email.com", "John", "Doe", "jdoe");

        Map<String, Object> expectedUpdates = Map.of(
                "emailAddress", "new@email.com",
                "givenName", "John",
                "familyName", "Doe",
                "name", "jdoe"
        );
        verify(client).updateUser("oid123", expectedUpdates);
    }

    @Test
    void testUpdateUserByUsername_UserNotFound() {
        when(client.searchUsers(anyString())).thenReturn(List.of());

        assertThrows(MidPointNotFoundException.class, () -> 
            userService.updateUserByUsername("nonexistent", "email", null, null, null)
        );
    }

    @Test
    void testUpdateUser_Validation() {
        assertThrows(IllegalArgumentException.class, () -> 
            userService.updateUser("oid", null, null, null, null)
        );
    }
}
