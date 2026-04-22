package com.sonora.sonora.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/** REST controller exposing Spotify search passthrough endpoints. */
@RestController
@RequestMapping("/api/spotify")
@Tag(name = "Spotify", description = "Public Spotify proxy endpoints")
public class SpotifyController {

    /** Safety margin before token expiration in milliseconds. */
    private static final long TOKEN_EXPIRY_MARGIN_MS = 60_000L;

    /** Spotify token endpoint URL. */
    private static final String TOKEN_URL =
            "https://accounts.spotify.com/api/token";

    /** Spotify search endpoint URL template. */
    private static final String SEARCH_URL =
            "https://api.spotify.com/v1/search?"
                    + "q={q}&type=track&limit=10&market=FR";

    /** HTTP timeout in milliseconds. */
    private static final int HTTP_TIMEOUT_MS = 5000;

    /** Converts expires_in seconds to milliseconds. */
    private static final long MS_PER_SECOND = 1000L;

    /** Spotify client identifier. */
    @Value("${SPOTIFY_CLIENT_ID:}")
    private String clientId;

    /** Spotify client secret. */
    @Value("${SPOTIFY_CLIENT_SECRET:}")
    private String clientSecret;

    /** HTTP client used for Spotify API calls. */
    private final RestTemplate restTemplate;

    /** Cached OAuth access token. */
    private String cachedToken;

    /** Cached token absolute expiry timestamp in milliseconds. */
    private long tokenExpiryMs;

    /**
     * Creates Spotify controller with bounded HTTP timeouts.
     */
    public SpotifyController() {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(HTTP_TIMEOUT_MS);
        factory.setReadTimeout(HTTP_TIMEOUT_MS);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Retrieves a Spotify access token, using cache when possible.
     *
     * @return valid access token
     */
    private String getAccessToken() {
        if (cachedToken != null
                && System.currentTimeMillis()
                < tokenExpiryMs - TOKEN_EXPIRY_MARGIN_MS) {
            return cachedToken;
        }

        String credentials = Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + credentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        Map<?, ?> response = restTemplate.postForObject(
                TOKEN_URL,
                new HttpEntity<>(body, headers),
                Map.class
        );

        if (response == null || !response.containsKey("access_token")) {
            throw new IllegalStateException(
                    "Reponse token Spotify invalide ou vide");
        }

        cachedToken = (String) response.get("access_token");
        int expiresIn = (Integer) response.get("expires_in");
        tokenExpiryMs =
                System.currentTimeMillis() + (expiresIn * MS_PER_SECOND);
        return cachedToken;
    }

    /**
     * Searches tracks on Spotify.
     *
     * @param q search query
     * @return Spotify search payload
     */
    @GetMapping("/search")
    @Operation(summary = "Search tracks on Spotify")
    public ResponseEntity<Object> search(@RequestParam final String q) {
        String accessToken;
        try {
            accessToken = getAccessToken();
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of(
                            "error",
                            "Impossible d'obtenir le token Spotify : "
                                    + ex.getMessage()));
        }

        HttpHeaders searchHeaders = new HttpHeaders();
        searchHeaders.set("Authorization", "Bearer " + accessToken);

        try {
            ResponseEntity<Object> spotifyResponse = restTemplate.exchange(
                    SEARCH_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(searchHeaders),
                    Object.class,
                    q
            );
            return ResponseEntity.ok(spotifyResponse.getBody());
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of(
                            "error",
                            "Erreur lors de la recherche Spotify : "
                                    + ex.getMessage()));
        }
    }
}
