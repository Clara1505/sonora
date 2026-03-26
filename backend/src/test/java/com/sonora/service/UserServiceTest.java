package com.sonora.service;

import com.sonora.dto.AuthResponse;
import com.sonora.dto.LoginRequest;
import com.sonora.dto.RegisterRequest;
import com.sonora.model.Role;
import com.sonora.model.User;
import com.sonora.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void register_withNewEmail_shouldReturnToken() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@sonora.com");
        req.setPassword("password123");

        when(userRepository.existsByEmail("new@sonora.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = userService.register(req);

        assertEquals("jwt-token", response.getToken());
        assertEquals("new@sonora.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_withExistingEmail_shouldThrowException() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("existing@sonora.com");
        req.setPassword("password123");

        when(userRepository.existsByEmail("existing@sonora.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@sonora.com");
        req.setPassword("password123");

        User user = User.builder()
            .email("test@sonora.com")
            .passwordHash("hashed")
            .role(Role.USER)
            .build();

        when(userRepository.findByEmail("test@sonora.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = userService.login(req);

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_withWrongPassword_shouldThrowException() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@sonora.com");
        req.setPassword("wrong");

        User user = User.builder().email("test@sonora.com").passwordHash("hashed").build();

        when(userRepository.findByEmail("test@sonora.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.login(req));
    }
}
