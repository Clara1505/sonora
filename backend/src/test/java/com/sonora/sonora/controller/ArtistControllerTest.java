package com.sonora.sonora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonora.sonora.dto.ArtistCreateUpdateDto;
import com.sonora.sonora.dto.ArtistDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.service.ArtistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtistController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArtistService artistService;

    @Test
    void shouldCreateArtist() throws Exception {
        ArtistCreateUpdateDto request = new ArtistCreateUpdateDto("Daft Punk", "French duo", "img");
        ArtistDto response = new ArtistDto(1L, "Daft Punk", "French duo", "img");

        when(artistService.create(any(ArtistCreateUpdateDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Daft Punk"));
    }

    @Test
    void shouldReadArtistById() throws Exception {
        when(artistService.getById(1L)).thenReturn(new ArtistDto(1L, "Daft Punk", "French duo", "img"));

        mockMvc.perform(get("/api/artists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Daft Punk"));
    }

    @Test
    void shouldReadAllArtists() throws Exception {
        when(artistService.getAll()).thenReturn(List.of(
                new ArtistDto(1L, "Daft Punk", "French duo", "img")
        ));

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldUpdateArtist() throws Exception {
        ArtistCreateUpdateDto request = new ArtistCreateUpdateDto("Daft Punk Updated", "Bio", "img2");
        ArtistDto response = new ArtistDto(1L, "Daft Punk Updated", "Bio", "img2");

        when(artistService.update(eq(1L), any(ArtistCreateUpdateDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/artists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Daft Punk Updated"));
    }

    @Test
    void shouldDeleteArtist() throws Exception {
        mockMvc.perform(delete("/api/artists/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenArtistNotFound() throws Exception {
        when(artistService.getById(999L)).thenThrow(new ResourceNotFoundException("Artist not found: 999"));

        mockMvc.perform(get("/api/artists/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Artist not found: 999"));
    }

    @Test
    void shouldReturn404WhenDeletingMissingArtist() throws Exception {
        doThrow(new ResourceNotFoundException("Artist not found: 999")).when(artistService).delete(999L);

        mockMvc.perform(delete("/api/artists/999"))
                .andExpect(status().isNotFound());
    }
}
