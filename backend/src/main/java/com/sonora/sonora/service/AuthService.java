package com.sonora.sonora.service;

import com.sonora.sonora.dto.AuthResponse;
import com.sonora.sonora.dto.LoginRequest;
import com.sonora.sonora.dto.RegisterRequest;
import com.sonora.sonora.model.entity.User;
import com.sonora.sonora.model.enums.Role;
import com.sonora.sonora.repository.UserRepository;
import com.sonora.sonora.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Service handling user registration and authentication. */
@Service
public class AuthService {

    /** Repository for user persistence operations. */
    private final UserRepository userRepository;

    /** Encoder used to hash and verify passwords. */
    private final PasswordEncoder passwordEncoder;

    /** Service used to generate JWT tokens. */
    private final JwtService jwtService;

    /**
     * Creates a new authentication service.
     *
     * @param repository user repository
     * @param encoder password encoder
     * @param service JWT service
     */
    public AuthService(final UserRepository repository,
                       final PasswordEncoder encoder,
                       final JwtService service) {
        this.userRepository = repository;
        this.passwordEncoder = encoder;
        this.jwtService = service;
    }

    /**
     * Registers a new user with the default USER role.
     *
     * @param request registration payload
     * @return authentication response containing JWT token
     * @throws IllegalArgumentException si l'email est déjà utilisé
     */
    public AuthResponse register(final RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(
            user.getEmail(),
            user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    /**
         * Authenticates an existing user.
         *
         * @param request login payload
         * @return authentication response containing JWT token
     * @throws IllegalArgumentException si les identifiants sont incorrects
     */
        public AuthResponse login(final LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException(
                "Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "Email ou mot de passe incorrect");
        }

        String token = jwtService.generateToken(
            user.getEmail(),
            user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
