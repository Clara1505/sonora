package com.sonora.sonora.service;

import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.dto.TrackUploadRequest;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.kafka.TrackEventProducer;
import com.sonora.sonora.kafka.TrackImportedEvent;
import com.sonora.sonora.model.entity.Artist;
import com.sonora.sonora.model.entity.Track;
import com.sonora.sonora.repository.ArtistRepository;
import com.sonora.sonora.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamingService {

    private static final List<String> ALLOWED_TYPES = List.of(
            "audio/mpeg", "audio/mp3", "audio/wav", "audio/x-wav"
    );

    private final TrackRepository    trackRepository;
    private final ArtistRepository   artistRepository;
    private final TrackEventProducer trackEventProducer;

    @Value("${audio.storage.path:/app/audio}")
    private String audioStoragePath;


    public TrackDto uploadTrack(MultipartFile file,
                                TrackUploadRequest request,
                                String uploadedByEmail) throws IOException {
        validateAudioFile(file);

        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist not found: " + request.getArtistId()));

        String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());
        Path storage = Path.of(audioStoragePath);
        Files.createDirectories(storage);
        try (InputStream in  = file.getInputStream();
             OutputStream out = Files.newOutputStream(storage.resolve(filename))) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        Track track = Track.builder()
                .title(request.getTitle())
                .duration(request.getDuration())
                .genre(request.getGenre())
                .mood(request.getMood())
                .filePath(filename)
                .artist(artist)
                .build();

        Track saved = trackRepository.save(track);
        log.info("Track sauvegardé : id={}, fichier={}", saved.getId(), filename);

        trackEventProducer.sendTrackImported(TrackImportedEvent.builder()
                .trackId(saved.getId())
                .filePath(filename)
                .title(saved.getTitle())
                .uploadedByEmail(uploadedByEmail)
                .build());

        return toDto(saved);
    }


    public ResponseEntity<Resource> streamTrack(Long trackId, HttpHeaders requestHeaders)
            throws MalformedURLException {

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Track not found: " + trackId));

        Path filePath = Path.of(audioStoragePath).resolve(track.getFilePath());
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Track not found: " + trackId);
        }

        Resource resource = new UrlResource(filePath.toUri());
        long fileLength;
        try {
            fileLength = Files.size(filePath);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Track not found: " + trackId);
        }

        MediaType mediaType = track.getFilePath().endsWith(".wav")
                ? MediaType.parseMediaType("audio/wav")
                : MediaType.parseMediaType("audio/mpeg");

        if (requestHeaders.getRange().isEmpty()) {
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(fileLength)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(resource);
        }

        HttpRange range    = requestHeaders.getRange().get(0);
        long start         = range.getRangeStart(fileLength);
        long end           = range.getRangeEnd(fileLength);
        long contentLength = end - start + 1;

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(mediaType)
                .contentLength(contentLength)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE,
                        "bytes " + start + "-" + end + "/" + fileLength)
                .body(new RangeResource(resource, start, contentLength));
    }


    private TrackDto toDto(Track track) {
        return new TrackDto(
                track.getId(),
                track.getTitle(),
                track.getDuration(),
                track.getGenre(),
                track.getMood(),
                track.getFilePath(),
                track.getAlbum()  != null ? track.getAlbum().getId()   : null,
                track.getArtist() != null ? track.getArtist().getId()  : null
        );
    }

    private void validateAudioFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        String ct = file.getContentType();
        if (ct == null || !ALLOWED_TYPES.contains(ct)) {
            throw new IllegalArgumentException(
                    "Format non supporté. Seuls MP3 et WAV sont acceptés. Reçu : " + ct);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".mp3";
        return filename.substring(filename.lastIndexOf('.'));
    }
}