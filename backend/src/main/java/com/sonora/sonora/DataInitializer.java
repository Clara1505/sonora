package com.sonora.sonora;

import com.sonora.sonora.model.entity.User;
import com.sonora.sonora.model.enums.Role;
import com.sonora.sonora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public final class DataInitializer implements CommandLineRunner {

    /** Repository for user entities. */
    private final UserRepository userRepository;

    /** Password encoder used to hash initial admin password. */
    private final PasswordEncoder passwordEncoder;

    /** Default admin email configured via properties. */
    @Value("${admin.email:admin@sonora.com}")
    private String adminEmail;

    /** Default admin password configured via properties. */
    @Value("${admin.password:Admin1234!}")
    private String adminPassword;

    @Override
    public void run(final String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Admin user created: {}", adminEmail);
        } else {
            log.info("Admin user already exists: {}", adminEmail);
        }
    }
}
