package com.sonora.sonora.service;

import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.dto.TrackUploadRequest;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.kafka.TrackEventProducer;
import com.sonora.sonora.kafka.TrackImportedEvent;
import com.sonora.sonora.kafka.TrackPlayedEvent;
import com.sonora.sonora.kafka.TrackPlayedProducer;
import com.sonora.sonora.model.entity.Artist;
import com.sonora.sonora.model.entity.Track;
import com.sonora.sonora.repository.ArtistRepository;
import com.sonora.sonora.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreamingServiceTest {

    @Mock private TrackRepository    trackRepository;
    @Mock private ArtistRepository   artistRepository;
    @Mock private TrackEventProducer trackEventProducer;
    @Mock private TrackPlayedProducer trackPlayedProducer;
    @InjectMocks private StreamingService streamingService;

    @TempDir Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(streamingService, "audioStoragePath", tempDir.toString());
    }

    @Test
    void uploadTrack_withValidMp3_shouldSaveAndPublishKafkaEvent() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", "fake-mp3".getBytes());
        TrackUploadRequest request = new TrackUploadRequest();
        request.setTitle("Test Track");
        request.setArtistId(1L);
        request.setDuration(200);

        Artist artist = Artist.builder().id(1L).name("Artiste Test").build();
        Track  saved  = Track.builder().id(10L).title("Test Track")
                             .duration(200).filePath("uuid.mp3").artist(artist).build();

        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(trackRepository.save(any())).thenReturn(saved);

        TrackDto response = streamingService.uploadTrack(file, request, "user@sonora.com");

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("Test Track");
        verify(trackRepository).save(any());

        ArgumentCaptor<TrackImportedEvent> captor = ArgumentCaptor.forClass(TrackImportedEvent.class);
        verify(trackEventProducer).sendTrackImported(captor.capture());
        assertThat(captor.getValue().getTrackId()).isEqualTo(10L);
        assertThat(captor.getValue().getUploadedByEmail()).isEqualTo("user@sonora.com");
    }

    @Test
    void uploadTrack_withInvalidContentType_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", "fake".getBytes());
        TrackUploadRequest request = new TrackUploadRequest();
        request.setArtistId(1L);

        assertThatThrownBy(() -> streamingService.uploadTrack(file, request, "u"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Format non supporté");

        verifyNoInteractions(trackRepository, artistRepository, trackEventProducer);
    }

    @Test
    void streamTrack_withoutRangeHeader_shouldReturn200() throws Exception {
        Files.write(tempDir.resolve("track.mp3"), "full audio".getBytes());
        Track track = Track.builder().id(1L).filePath("track.mp3").build();
        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));

        ResponseEntity<Resource> resp = streamingService.streamTrack(1L, new HttpHeaders());

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getHeaders().getFirst(HttpHeaders.ACCEPT_RANGES)).isEqualTo("bytes");
    }

    @Test
    void streamTrack_withRangeHeader_shouldReturn206() throws Exception {
        Files.write(tempDir.resolve("track.mp3"), "hello streaming world".getBytes());
        Track track = Track.builder().id(1L).filePath("track.mp3").build();
        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.RANGE, "bytes=0-4");

        ResponseEntity<Resource> resp = streamingService.streamTrack(1L, headers);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.PARTIAL_CONTENT);
        assertThat(resp.getHeaders().getFirst(HttpHeaders.CONTENT_RANGE)).startsWith("bytes 0-4/");
    }

    @Test
    void streamTrack_withUnknownId_shouldThrow404() {
        when(trackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> streamingService.streamTrack(99L, new HttpHeaders()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void streamTrack_whenFileDeletedFromDisk_shouldThrow404() {
        Track track = Track.builder().id(1L).filePath("missing.mp3").build();
        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));

        assertThatThrownBy(() -> streamingService.streamTrack(1L, new HttpHeaders()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void publishTrackPlayed_withExistingTrack_shouldPublishKafkaEvent() {
        when(trackRepository.existsById(10L)).thenReturn(true);

        streamingService.publishTrackPlayed(10L, "user@sonora.com", 95);

        ArgumentCaptor<TrackPlayedEvent> captor = ArgumentCaptor.forClass(TrackPlayedEvent.class);
        verify(trackPlayedProducer).sendTrackPlayed(captor.capture());
        assertThat(captor.getValue().getTrackId()).isEqualTo(10L);
        assertThat(captor.getValue().getUserEmail()).isEqualTo("user@sonora.com");
        assertThat(captor.getValue().getDurationListened()).isEqualTo(95);
    }

    @Test
    void publishTrackPlayed_withUnknownTrack_shouldThrow404() {
        when(trackRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> streamingService.publishTrackPlayed(99L, "user@sonora.com", 95))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(trackPlayedProducer, never()).sendTrackPlayed(any());
    }
}