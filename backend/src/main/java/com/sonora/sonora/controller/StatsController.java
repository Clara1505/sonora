package com.sonora.sonora.controller;

import com.sonora.sonora.dto.TrackStatsResponse;
import com.sonora.sonora.dto.UserStatsResponse;
import com.sonora.sonora.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/tracks/{trackId}")
    public TrackStatsResponse getTrackStats(@PathVariable Long trackId) {
        return statsService.getTrackStats(trackId);
    }

    @GetMapping("/me")
    public UserStatsResponse getMyStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        return statsService.getUserStats(userDetails.getUsername());
    }
}