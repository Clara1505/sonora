package com.sonora.sonora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Authentication response payload returned after login or registration. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** JWT access token. */
    private String token;

    /** Authenticated user email. */
    private String email;

    /** Authenticated user role name. */
    private String role;
}
