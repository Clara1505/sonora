package com.sonora.sonora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonora.sonora.dto.AlbumCreateUpdateDto;
import com.sonora.sonora.dto.AlbumDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.security.JwtService;
import com.sonora.sonora.service.AlbumService;
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

@WebMvcTest(AlbumController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AlbumService albumService;

    @Test
    void shouldCreateAlbum() throws Exception {
        AlbumCreateUpdateDto request = new AlbumCreateUpdateDto("RAM", 2013, "cover", 1L);
        AlbumDto response = new AlbumDto(1L, "RAM", 2013, "cover", 1L);

        when(albumService.create(any(AlbumCreateUpdateDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("RAM"));
    }

    @Test
    void shouldReadAlbumById() throws Exception {
        when(albumService.getById(1L)).thenReturn(new AlbumDto(1L, "RAM", 2013, "cover", 1L));

        mockMvc.perform(get("/api/albums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("RAM"));
    }

    @Test
    void shouldReadAllAlbums() throws Exception {
        when(albumService.getAll()).thenReturn(List.of(
                new AlbumDto(1L, "RAM", 2013, "cover", 1L)
        ));

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldUpdateAlbum() throws Exception {
        AlbumCreateUpdateDto request = new AlbumCreateUpdateDto("RAM 2", 2014, "cover2", 1L);
        AlbumDto response = new AlbumDto(1L, "RAM 2", 2014, "cover2", 1L);

        when(albumService.update(eq(1L), any(AlbumCreateUpdateDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/albums/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("RAM 2"));
    }

    @Test
    void shouldDeleteAlbum() throws Exception {
        mockMvc.perform(delete("/api/albums/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenAlbumNotFound() throws Exception {
        when(albumService.getById(999L)).thenThrow(new ResourceNotFoundException("Album not found: 999"));

        mockMvc.perform(get("/api/albums/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Album not found: 999"));
    }

    @Test
    void shouldReturn404WhenDeletingMissingAlbum() throws Exception {
        doThrow(new ResourceNotFoundException("Album not found: 999")).when(albumService).delete(999L);

        mockMvc.perform(delete("/api/albums/999"))
                .andExpect(status().isNotFound());
    }
}
