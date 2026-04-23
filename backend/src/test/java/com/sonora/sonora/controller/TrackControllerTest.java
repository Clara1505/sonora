package com.sonora.sonora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonora.sonora.dto.TrackCreateUpdateDto;
import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import com.sonora.sonora.security.JwtService;
import com.sonora.sonora.service.TrackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrackController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TrackService trackService;

    @Test
    void shouldCreateTrack() throws Exception {
        TrackCreateUpdateDto request = new TrackCreateUpdateDto("Get Lucky", 248, Genre.POP, Mood.HAPPY, "song.mp3", null, 1L);
        TrackDto response = new TrackDto(1L, "Get Lucky", 248, Genre.POP, Mood.HAPPY, "song.mp3", null, 1L, null);

        when(trackService.create(any(TrackCreateUpdateDto.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Get Lucky"));
    }

    @Test
    void shouldReadTrackById() throws Exception {
        when(trackService.getById(1L)).thenReturn(new TrackDto(1L, "Get Lucky", 248, Genre.POP, Mood.HAPPY, "song.mp3", null, 1L, null));

        mockMvc.perform(get("/api/tracks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Get Lucky"));
    }

    @Test
    void shouldReadAllTracks() throws Exception {
        when(trackService.getAll()).thenReturn(List.of(
                new TrackDto(1L, "Get Lucky", 248, Genre.POP, Mood.HAPPY, "song.mp3", null, 1L, null)
        ));

        mockMvc.perform(get("/api/tracks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldUpdateTrack() throws Exception {
        TrackCreateUpdateDto request = new TrackCreateUpdateDto("Get Lucky Remix", 260, Genre.POP, Mood.CHILL, "song2.mp3", null, 1L);
        TrackDto response = new TrackDto(1L, "Get Lucky Remix", 260, Genre.POP, Mood.CHILL, "song2.mp3", null, 1L, null);

        when(trackService.update(eq(1L), any(TrackCreateUpdateDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/tracks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Get Lucky Remix"));
    }

    @Test
    void shouldDeleteTrack() throws Exception {
        mockMvc.perform(delete("/api/tracks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenTrackNotFound() throws Exception {
        when(trackService.getById(999L)).thenThrow(new ResourceNotFoundException("Track not found: 999"));

        mockMvc.perform(get("/api/tracks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Track not found: 999"));
    }

    @Test
    void shouldReturn404WhenDeletingMissingTrack() throws Exception {
        doThrow(new ResourceNotFoundException("Track not found: 999"))
                .when(trackService).delete(eq(999L), any(), anyBoolean());

        mockMvc.perform(delete("/api/tracks/999"))
                .andExpect(status().isNotFound());
    }
}
