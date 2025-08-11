package io.github.cristhianm30.heikoh.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void testGettersAndSetters() {
        UserModel user = new UserModel();
        LocalDateTime now = LocalDateTime.now();

        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRole("USER");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.getEnabled());
        assertEquals("USER", user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        UserModel user = UserModel.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .enabled(true)
                .role("USER")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.getEnabled());
        assertEquals("USER", user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        UserModel user = new UserModel();
        assertNotNull(user);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        UserModel user = new UserModel(
                1L, "testuser", "test@example.com", "password",
                true, "USER", now, now);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.getEnabled());
        assertEquals("USER", user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }





}