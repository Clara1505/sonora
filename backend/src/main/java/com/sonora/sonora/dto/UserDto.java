package com.sonora.sonora.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String email,
        String role,
        LocalDateTime createdAt
) {
}
