package com.sonora.sonora.controller;

import com.sonora.sonora.dto.FavoriteResponse;
import com.sonora.sonora.exception.AlreadyFavoriteException;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.service.FavoriteService;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired  private MockMvc mockMvc;
    @MockitoBean private FavoriteService favoriteService;


    @Test
    @WithMockUser(username = "user@sonora.com")
    void addFavorite_shouldReturn201() throws Exception {
        FavoriteResponse response = FavoriteResponse.builder()
                .id(1L).trackId(10L).trackTitle("Ma chanson").build();

        when(favoriteService.addFavorite(eq(10L), any())).thenReturn(response);

        mockMvc.perform(post("/api/favorites/10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trackId").value(10))
                .andExpect(jsonPath("$.trackTitle").value("Ma chanson"));
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void addFavorite_whenDuplicate_shouldReturn409() throws Exception {
        when(favoriteService.addFavorite(eq(10L), any()))
                .thenThrow(new AlreadyFavoriteException(10L));

        mockMvc.perform(post("/api/favorites/10"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Track already in favorites: 10"));
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void removeFavorite_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/favorites/10"))
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void removeFavorite_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Favorite not found for track: 99"))
                .when(favoriteService).removeFavorite(eq(99L), any());

        mockMvc.perform(delete("/api/favorites/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Favorite not found for track: 99"));
    }


    @Test
    @WithMockUser(username = "user@sonora.com")
    void listFavorites_shouldReturn200WithList() throws Exception {
        List<FavoriteResponse> favorites = List.of(
                FavoriteResponse.builder().id(1L).trackId(10L).trackTitle("Ma chanson").build(),
                FavoriteResponse.builder().id(2L).trackId(20L).trackTitle("Autre chanson").build()
        );

        when(favoriteService.listFavorites(any())).thenReturn(favorites);

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trackId").value(10))
                .andExpect(jsonPath("$[1].trackId").value(20));
    }
}