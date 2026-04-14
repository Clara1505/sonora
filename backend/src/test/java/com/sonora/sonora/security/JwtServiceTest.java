package com.sonora.sonora.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Clé de 256 bits minimum pour HMAC-SHA256
        String secret = "TestSecretKeySonora2025MustBeAtLeast256BitsLongForTests!!";
        jwtService = new JwtService(secret, 3600000L); // 1h
    }

    @Test
    @DisplayName("Génère un token non-null")
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtService.generateToken("ines@sonora.com", "USER");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Extrait l'email du token")
    void extractEmail_shouldReturnCorrectEmail() {
        String token = jwtService.generateToken("lucas@sonora.com", "USER");
        assertEquals("lucas@sonora.com", jwtService.extractEmail(token));
    }

    @Test
    @DisplayName("Extrait le rôle du token")
    void extractRole_shouldReturnCorrectRole() {
        String token = jwtService.generateToken("marc@sonora.com", "ADMIN");
        assertEquals("ADMIN", jwtService.extractRole(token));
    }

    @Test
    @DisplayName("Token valide → true")
    void isTokenValid_validToken_shouldReturnTrue() {
        String token = jwtService.generateToken("ines@sonora.com", "USER");
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    @DisplayName("Token falsifié → false")
    void isTokenValid_tamperedToken_shouldReturnFalse() {
        String token = jwtService.generateToken("ines@sonora.com", "USER");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtService.isTokenValid(tampered));
    }

    @Test
    @DisplayName("Token vide → false")
    void isTokenValid_emptyToken_shouldReturnFalse() {
        assertFalse(jwtService.isTokenValid(""));
    }

    @Test
    @DisplayName("Token expiré → false")
    void isTokenValid_expiredToken_shouldReturnFalse() {
        // Service avec expiration de 0ms → token expire immédiatement
        JwtService expiredService = new JwtService(
                "TestSecretKeySonora2025MustBeAtLeast256BitsLongForTests!!", 0L);
        String token = expiredService.generateToken("ines@sonora.com", "USER");

        // Petite pause pour s'assurer que le token est expiré
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        assertFalse(expiredService.isTokenValid(token));
    }

    @Test
    @DisplayName("Token signé avec une autre clé → false")
    void isTokenValid_wrongKey_shouldReturnFalse() {
        JwtService otherService = new JwtService(
                "AnotherSecretKeyThatIsDefinitelyDifferentFromTheFirst!!", 3600000L);
        String token = otherService.generateToken("ines@sonora.com", "USER");

        assertFalse(jwtService.isTokenValid(token));
    }
}
