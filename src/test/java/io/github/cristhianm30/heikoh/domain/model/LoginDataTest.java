package io.github.cristhianm30.heikoh.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginDataTest {

    @Test
    void testGettersAndSetters() {
        LoginData loginData = new LoginData();

        loginData.setToken("testToken");
        loginData.setUsername("testuser");
        loginData.setEmail("test@example.com");
        loginData.setEnabled(true);
        loginData.setRole("USER");
        loginData.setId(1L);

        assertEquals("testToken", loginData.getToken());
        assertEquals("testuser", loginData.getUsername());
        assertEquals("test@example.com", loginData.getEmail());
        assertTrue(loginData.getEnabled());
        assertEquals("USER", loginData.getRole());
        assertEquals(1L, loginData.getId());
    }

    @Test
    void testBuilder() {
        LoginData loginData = LoginData.builder()
                .token("testToken")
                .username("testuser")
                .email("test@example.com")
                .enabled(true)
                .role("USER")
                .id(1L)
                .build();

        assertEquals("testToken", loginData.getToken());
        assertEquals("testuser", loginData.getUsername());
        assertEquals("test@example.com", loginData.getEmail());
        assertTrue(loginData.getEnabled());
        assertEquals("USER", loginData.getRole());
        assertEquals(1L, loginData.getId());
    }

    @Test
    void testNoArgsConstructor() {
        LoginData loginData = new LoginData();
        assertNotNull(loginData);
    }

    @Test
    void testAllArgsConstructor() {
        LoginData loginData = new LoginData(
                "testToken", "testuser", "test@example.com",
                true, "USER", 1L);

        assertEquals("testToken", loginData.getToken());
        assertEquals("testuser", loginData.getUsername());
        assertEquals("test@example.com", loginData.getEmail());
        assertTrue(loginData.getEnabled());
        assertEquals("USER", loginData.getRole());
        assertEquals(1L, loginData.getId());
    }



}