package com.sonora.security;

import com.sonora.model.Role;
import com.sonora.model.User;
import com.sonora.repository.UserRepository;
import com.sonora.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock private UserRepository userRepository;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void doFilter_withValidToken_shouldSetAuthentication() throws Exception {
        User user = User.builder()
            .email("test@sonora.com")
            .passwordHash("hashed")
            .role(Role.USER)
            .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.extractEmail("valid-token")).thenReturn("test@sonora.com");
        when(userRepository.findByEmail("test@sonora.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("valid-token", user)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_withNoToken_shouldContinueFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }
}
