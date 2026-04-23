package com.sonora.sonora.dto;

public record AdminStatsDto(
        long userCount,
        long trackCount,
        long playlistCount
) {
}
