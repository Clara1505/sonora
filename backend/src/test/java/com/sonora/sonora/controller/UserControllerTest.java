package com.sonora.sonora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonora.sonora.dto.LoginRequest;
import com.sonora.sonora.dto.RegisterRequest;
import com.sonora.sonora.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    // ─── REGISTER ────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/auth/register")
    class RegisterTests {

        @Test
        @DisplayName("Register OK → 201 + token retourné")
        void register_shouldReturn201WithToken() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("ines@sonora.com")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.email").value("ines@sonora.com"))
                    .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        @DisplayName("Register avec email dupliqué → 400")
        void register_duplicateEmail_shouldReturn400() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("ines@sonora.com")
                    .password("password123")
                    .build();

            // Premier register OK
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // Deuxième register KO
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Cet email est déjà utilisé"));
        }

        @Test
        @DisplayName("Register avec email invalide → 400")
        void register_invalidEmail_shouldReturn400() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("pas-un-email")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());
        }

        @Test
        @DisplayName("Register avec mot de passe trop court → 400")
        void register_shortPassword_shouldReturn400() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("ines@sonora.com")
                    .password("123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());
        }
    }

    // ─── LOGIN ───────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginTests {

        @BeforeEach
        void createUser() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("lucas@sonora.com")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Login OK → 200 + token retourné")
        void login_shouldReturn200WithToken() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .email("lucas@sonora.com")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.email").value("lucas@sonora.com"))
                    .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        @DisplayName("Login avec mauvais mot de passe → 400")
        void login_wrongPassword_shouldReturn400() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .email("lucas@sonora.com")
                    .password("wrongpassword")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email ou mot de passe incorrect"));
        }

        @Test
        @DisplayName("Login avec email inexistant → 400")
        void login_unknownEmail_shouldReturn400() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .email("inconnu@sonora.com")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email ou mot de passe incorrect"));
        }
    }

    // ─── TOKEN INVALIDE → 401 ────────────────────────────────────

    @Nested
    @DisplayName("Accès avec token invalide")
    class TokenSecurityTests {

        @Test
        @DisplayName("Requête protégée sans token → 401")
        void protectedEndpoint_noToken_shouldReturn401() throws Exception {
            mockMvc.perform(get("/api/tracks"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Requête protégée avec token invalide → 401")
        void protectedEndpoint_invalidToken_shouldReturn401() throws Exception {
            mockMvc.perform(get("/api/tracks")
                            .header("Authorization", "Bearer token.invalide.ici"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Requête protégée avec token valide → pas de 401")
        void protectedEndpoint_validToken_shouldNotReturn401() throws Exception {
            // D'abord on s'inscrit pour obtenir un token valide
            RegisterRequest request = RegisterRequest.builder()
                    .email("marc@sonora.com")
                    .password("password123")
                    .build();

            MvcResult result = mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String token = objectMapper.readTree(
                    result.getResponse().getContentAsString()
            ).get("token").asText();

            // Avec un token valide, on ne doit pas avoir 401
            // (on aura probablement 404 car /api/tracks n'existe pas encore, mais pas 401)
            mockMvc.perform(get("/api/tracks")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is(not(401)));
        }
    }
}
