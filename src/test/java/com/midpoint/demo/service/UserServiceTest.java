package com.midpoint.demo.service;

import com.midpoint.demo.cli.client.MidPointClient;
import com.midpoint.demo.domain.User;
import com.midpoint.demo.exception.MidPointNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private MidPointClient client;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .oid("123")
                .name("testuser")
                .emailAddress("test@example.com")
                .build();
    }

    @Test
    void shouldReturnUsers_whenSearchByUsername() {
        when(client.searchUsers("testuser")).thenReturn(List.of(testUser));

        List<User> result = userService.searchByUsername("testuser");

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getName());
        verify(client).searchUsers("testuser");
    }

    @Test
    void shouldUpdateUser_whenOidResolvedFromCache() {
        when(client.searchUsers("testuser")).thenReturn(List.of(testUser));

        userService.updateUserByUsername("testuser", "new@example.com", null, null, null);

        userService.updateUserByUsername("testuser", "another@example.com", null, null, null);

        verify(client, times(1)).searchUsers("testuser");
        verify(client, times(2)).updateUser(eq("123"), anyMap());
    }

    @Test
    void shouldThrowNotFoundException_whenUserNotFoundDuringUpdate() {
        when(client.searchUsers("unknown")).thenReturn(List.of());

        assertThrows(MidPointNotFoundException.class, () ->
            userService.updateUserByUsername("unknown", "email@test.com", null, null, null));
    }

    @Test
    void shouldThrowIllegalArgumentException_whenNoFieldsProvidedForUpdate() {
        assertThrows(IllegalArgumentException.class, () ->
            userService.updateUser("123", null, null, null, null));
    }

    @Test
    void shouldUpdateCache_whenUsernameIsChanged() {
        when(client.searchUsers("olduser")).thenReturn(List.of(testUser));

        userService.updateUserByUsername("olduser", null, null, null, "newuser");

        userService.updateUserByUsername("newuser", "new@email.com", null, null, null);
        verify(client, times(0)).searchUsers("newuser");
        verify(client, times(1)).searchUsers("olduser");
    }
}
