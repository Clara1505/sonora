package com.sonora.sonora.controller;

import com.sonora.sonora.dto.TrackStatsResponse;
import com.sonora.sonora.dto.UserStatsResponse;
import com.sonora.sonora.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for statistics endpoints. */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Access listening statistics")
@SecurityRequirement(name = "bearerAuth")
public class StatsController {

    /** Statistics service. */
    private final StatsService statsService;

    /**
     * Returns aggregated statistics for one track.
     *
     * @param trackId track identifier
     * @return track statistics
     */
    @GetMapping("/tracks/{trackId}")
    @Operation(summary = "Get track statistics")
    public TrackStatsResponse getTrackStats(@PathVariable final Long trackId) {
        return statsService.getTrackStats(trackId);
    }

    /**
     * Returns aggregated statistics for the authenticated user.
     *
     * @param email authenticated user email
     * @return user statistics
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user statistics")
    public UserStatsResponse getMyStats(
            @AuthenticationPrincipal final String email) {
        return statsService.getUserStats(email);
    }
}
