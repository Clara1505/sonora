package com.sonora.sonora.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/** Spring Security configuration for API authentication and authorization. */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** JWT authentication filter inserted before username/password filter. */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Creates security configuration.
     *
     * @param authenticationFilter JWT authentication filter
     */
    public SecurityConfig(final JwtAuthenticationFilter authenticationFilter) {
        this.jwtAuthenticationFilter = authenticationFilter;
    }

    /**
     * Configures HTTP security filter chain.
     *
     * @param http HTTP security builder
     * @return configured filter chain
     * @throws Exception if security setup fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http)
            throws Exception {
        http
            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(
                    "/api/auth/**"
                ).permitAll();
                auth.requestMatchers(
                    "/api/tracks/*/stream"
                ).permitAll();
                auth.requestMatchers(
                    "/swagger"
                ).permitAll();
                auth.requestMatchers(
                    "/swagger/"
                ).permitAll();
                auth.requestMatchers(
                    "/swagger-ui/**"
                ).permitAll();
                auth.requestMatchers(
                    "/v3/api-docs/**"
                ).permitAll();
                auth.requestMatchers(
                    "/api/spotify/**"
                ).permitAll();
                auth.requestMatchers(
                    "/actuator/health"
                ).permitAll();
                // Endpoints admin uniquement
                auth.requestMatchers(
                    "/api/admin/**"
                ).hasRole(
                    "ADMIN"
                );
                auth.requestMatchers(
                    "/api/playlists/all"
                ).hasRole(
                    "ADMIN"
                );
                auth.anyRequest().authenticated();
            })
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                    (request, response, authException) -> {
                        response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        String body = "{\"status\":401,"
                            + "\"message\":\"Non authentifié\"}";
                        response.getWriter().write(body);
                    })
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * Configures CORS settings.
     *
     * @return CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        // Autoriser l'en-tête Authorization pour les requêtes CORS
        config.setAllowedHeaders(
            List.of("Authorization", "Content-Type", "Accept"));
        // Exposer Authorization si nécessaire côté client
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Creates the password encoder bean.
     *
     * @return password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes authentication manager bean.
     *
     * @param authenticationConfiguration authentication configuration
     * @return authentication manager
     * @throws Exception if manager resolution fails
     */
    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
