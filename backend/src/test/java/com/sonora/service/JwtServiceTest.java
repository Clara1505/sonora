package com.sonora.service;

import com.sonora.model.Role;
import com.sonora.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // on injecte la clé via ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "secretKey",
            "dGVzdC1zZWNyZXQta2V5LXNvbm9yYS1qd3QtMjAyNA==");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);

        testUser = User.builder()
            .id("user-1")
            .email("test@sonora.com")
            .role(Role.USER)
            .build();
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtService.generateToken(testUser);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractEmail_shouldReturnCorrectEmail() {
        String token = jwtService.generateToken(testUser);
        String email = jwtService.extractEmail(token);
        assertEquals("test@sonora.com", email);
    }

    @Test
    void isTokenValid_withValidToken_shouldReturnTrue() {
        String token = jwtService.generateToken(testUser);
        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    void isTokenValid_withWrongUser_shouldReturnFalse() {
        String token = jwtService.generateToken(testUser);
        User otherUser = User.builder().email("other@sonora.com").build();
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }
}
