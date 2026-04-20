package com.sonora.sonora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonora.sonora.dto.ArtistDto;
import com.sonora.sonora.dto.PlaylistCreateDto;
import com.sonora.sonora.dto.PlaylistDto;
import com.sonora.sonora.dto.PlaylistTrackDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import com.sonora.sonora.model.enums.PlaylistType;
import com.sonora.sonora.security.JwtService;
import com.sonora.sonora.service.PlaylistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaylistController.class)
@AutoConfigureMockMvc(addFilters = false)
class PlaylistControllerTest {

    @Autowired  private MockMvc mockMvc;
    @Autowired  private ObjectMapper objectMapper;
    @MockitoBean private PlaylistService playlistService;
    @MockBean   private JwtService jwtService;

    private PlaylistDto mockDto(PlaylistType type) {
        ArtistDto artist = new ArtistDto(1L, "Daft Punk", null, null);
        PlaylistTrackDto track = new PlaylistTrackDto(1L, "Get Lucky", 248, Genre.POP, Mood.HAPPY, artist);
        return new PlaylistDto(1L, "Ma playlist", type, null, LocalDateTime.now(), List.of(track));
    }

    private PlaylistDto emptyDto(PlaylistType type) {
        return new PlaylistDto(1L, "Ma playlist", type, null, LocalDateTime.now(), List.of());
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void list_shouldReturn200WithPlaylists() throws Exception {
        when(playlistService.getUserPlaylists(any())).thenReturn(List.of(emptyDto(PlaylistType.MANUAL)));

        mockMvc.perform(get("/api/playlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ma playlist"))
                .andExpect(jsonPath("$[0].type").value("MANUAL"));
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void list_whenEmpty_shouldReturn200WithEmptyArray() throws Exception {
        when(playlistService.getUserPlaylists(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/playlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void create_manual_shouldReturn201() throws Exception {
        PlaylistCreateDto request = new PlaylistCreateDto("Ma playlist", PlaylistType.MANUAL, null, null);
        when(playlistService.create(any(), any())).thenReturn(emptyDto(PlaylistType.MANUAL));

        mockMvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ma playlist"))
                .andExpect(jsonPath("$.type").value("MANUAL"))
                .andExpect(jsonPath("$.tracks").isArray())
                .andExpect(jsonPath("$.tracks").isEmpty());
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void create_smart_shouldReturn201WithTracks() throws Exception {
        PlaylistCreateDto request = new PlaylistCreateDto("Pop Happy", PlaylistType.SMART, Mood.HAPPY, Genre.POP);
        when(playlistService.create(any(), any())).thenReturn(mockDto(PlaylistType.SMART));

        mockMvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("SMART"))
                .andExpect(jsonPath("$.tracks[0].title").value("Get Lucky"))
                .andExpect(jsonPath("$.tracks[0].artist.name").value("Daft Punk"));
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void create_smart_withGenreOnly_shouldReturn201() throws Exception {
        PlaylistCreateDto request = new PlaylistCreateDto("Rock Only", PlaylistType.SMART, null, Genre.ROCK);
        when(playlistService.create(any(), any())).thenReturn(mockDto(PlaylistType.SMART));

        mockMvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("SMART"));
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void getById_shouldReturn200() throws Exception {
        when(playlistService.getById(eq(1L), any())).thenReturn(mockDto(PlaylistType.MANUAL));

        mockMvc.perform(get("/api/playlists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ma playlist"));
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void getById_whenNotFound_shouldReturn404() throws Exception {
        when(playlistService.getById(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Playlist not found: 99"));

        mockMvc.perform(get("/api/playlists/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Playlist not found: 99"));
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/playlists/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void delete_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Playlist not found: 99"))
                .when(playlistService).delete(eq(99L), any());

        mockMvc.perform(delete("/api/playlists/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Playlist not found: 99"));
    }
}
