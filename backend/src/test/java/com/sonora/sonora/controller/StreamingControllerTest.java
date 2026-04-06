package com.sonora.sonora.controller;

import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import com.sonora.sonora.service.StreamingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StreamingController.class)
@AutoConfigureMockMvc(addFilters = false)
class StreamingControllerTest {

    @Autowired  private MockMvc mockMvc;
    @MockitoBean private StreamingService streamingService;

    @Test
    @WithMockUser(username = "user@sonora.com")
    void upload_withValidMp3_shouldReturn200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "song.mp3", "audio/mpeg", "fake-audio".getBytes());
        MockMultipartFile metadata = new MockMultipartFile(
                "metadata", "", "application/json",
                """
                {"title":"Ma chanson","artistId":1,"duration":200,"genre":"POP","mood":"HAPPY"}
                """.getBytes());

        TrackDto response = new TrackDto(1L, "Ma chanson", 200, Genre.POP, Mood.HAPPY, "uuid.mp3", null, 1L);
        when(streamingService.uploadTrack(any(), any(), any())).thenReturn(response);

        mockMvc.perform(multipart("/api/tracks/upload").file(file).file(metadata))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Ma chanson"));
    }

    @Test
    @WithMockUser
    void stream_withoutRangeHeader_shouldReturn200() throws Exception {
        when(streamingService.streamTrack(any(), any()))
                .thenReturn(ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .body(new ByteArrayResource("fake".getBytes())));

        mockMvc.perform(get("/api/tracks/1/stream"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCEPT_RANGES, "bytes"));
    }

    @Test
    @WithMockUser
    void stream_withRangeHeader_shouldReturn206() throws Exception {
        when(streamingService.streamTrack(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .header(HttpHeaders.CONTENT_RANGE, "bytes 0-4/1000")
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .body(new ByteArrayResource("hello".getBytes())));

        mockMvc.perform(get("/api/tracks/1/stream")
                        .header(HttpHeaders.RANGE, "bytes=0-4"))
                .andExpect(status().isPartialContent())
                .andExpect(header().string(HttpHeaders.CONTENT_RANGE, "bytes 0-4/1000"));
    }

    @Test
    @WithMockUser
    void stream_withUnknownId_shouldReturn404() throws Exception {
        when(streamingService.streamTrack(any(), any()))
                .thenThrow(new ResourceNotFoundException("Track not found: 99"));

        mockMvc.perform(get("/api/tracks/99/stream"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Track not found: 99"));
    }
}