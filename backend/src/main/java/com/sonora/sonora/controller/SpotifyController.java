package com.sonora.sonora.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    @Value("${SPOTIFY_CLIENT_ID:}")
    private String clientId;

    @Value("${SPOTIFY_CLIENT_SECRET:}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    // ── Cache du token ────────────────────────────────────────────
    private String cachedToken = null;
    private long tokenExpiryMs = 0;

    public SpotifyController() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    private String getAccessToken() {
        // On renouvelle uniquement si expiré (avec 60s de marge)
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryMs - 60_000) {
            return cachedToken;
        }

        String credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + credentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        Map<?, ?> response = restTemplate.postForObject(
            "https://accounts.spotify.com/api/token",
            new HttpEntity<>(body, headers),
            Map.class
        );

        if (response == null || !response.containsKey("access_token")) {
            throw new IllegalStateException("Réponse token Spotify invalide ou vide");
        }

        cachedToken = (String) response.get("access_token");
        int expiresIn = (Integer) response.get("expires_in"); // généralement 3600
        tokenExpiryMs = System.currentTimeMillis() + (expiresIn * 1000L);

        return cachedToken;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String q) {
        // ── 1. Token (depuis le cache ou Spotify) ─────────────────
        String accessToken;
        try {
            accessToken = getAccessToken();
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Impossible d'obtenir le token Spotify : " + e.getMessage()));
        }

        // ── 2. Recherche ──────────────────────────────────────────
        HttpHeaders searchHeaders = new HttpHeaders();
        searchHeaders.set("Authorization", "Bearer " + accessToken);

        try {
            ResponseEntity<Object> spotifyResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/search?q={q}&type=track&limit=10&market=FR",
                HttpMethod.GET,
                new HttpEntity<>(searchHeaders),
                Object.class,
                q
            );
            return ResponseEntity.ok(spotifyResponse.getBody());
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Erreur lors de la recherche Spotify : " + e.getMessage()));
        }
    }
}