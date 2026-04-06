package com.sonora.sonora.controller;

import com.sonora.sonora.dto.TrackStatsResponse;
import com.sonora.sonora.dto.UserStatsResponse;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.service.StatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
@AutoConfigureMockMvc(addFilters = false)
class StatsControllerTest {

    @Autowired   private MockMvc mockMvc;
    @MockitoBean private StatsService statsService;


    @Test
    @WithMockUser
    void getTrackStats_shouldReturn200() throws Exception {
        TrackStatsResponse response = TrackStatsResponse.builder()
                .trackId(1L).trackTitle("Ma chanson").playCount(5).totalDurationListened(1200L)
                .build();

        when(statsService.getTrackStats(1L)).thenReturn(response);

        mockMvc.perform(get("/api/stats/tracks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playCount").value(5))
                .andExpect(jsonPath("$.totalDurationListened").value(1200));
    }


    @Test
    @WithMockUser
    void getTrackStats_withUnknownId_shouldReturn404() throws Exception {
        when(statsService.getTrackStats(99L))
                .thenThrow(new ResourceNotFoundException("Track not found: 99"));

        mockMvc.perform(get("/api/stats/tracks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Track not found: 99"));
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void getMyStats_shouldReturn200() throws Exception {
        UserStatsResponse response = UserStatsResponse.builder()
                .userEmail("user@sonora.com")
                .totalPlays(10)
                .topTracks(List.of())
                .build();

        when(statsService.getUserStats(any())).thenReturn(response);

        mockMvc.perform(get("/api/stats/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlays").value(10))
                .andExpect(jsonPath("$.userEmail").value("user@sonora.com"));
    }
}