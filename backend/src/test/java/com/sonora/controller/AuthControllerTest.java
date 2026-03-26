package com.sonora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonora.controller.AuthController;
import com.sonora.dto.AuthResponse;
import com.sonora.dto.LoginRequest;
import com.sonora.dto.RegisterRequest;
import com.sonora.repository.UserRepository;
import com.sonora.security.SecurityConfig;
import com.sonora.service.JwtService;
import com.sonora.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean private UserService userService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean private UserRepository userRepository;

    @Test
    void register_shouldReturn201WithToken() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@sonora.com");
        req.setPassword("password123");

        AuthResponse response = new AuthResponse("jwt-token", "new@sonora.com", "USER");
        when(userService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").value("jwt-token"))
            .andExpect(jsonPath("$.email").value("new@sonora.com"));
    }

    @Test
    void register_withInvalidEmail_shouldReturn400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("pas-un-email");
        req.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn200WithToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@sonora.com");
        req.setPassword("password123");

        AuthResponse response = new AuthResponse("jwt-token", "test@sonora.com", "USER");
        when(userService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"));
    }
}
