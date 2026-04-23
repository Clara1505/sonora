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
import com.sonora.sonora.model.entity.User;
import com.sonora.sonora.repository.ArtistRepository;
import com.sonora.sonora.repository.TrackRepository;
import com.sonora.sonora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/** Service handling audio upload and HTTP byte-range streaming operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamingService {

    /** Allowed audio MIME types for upload. */
    private static final List<String> ALLOWED_TYPES = List.of(
            "audio/mpeg", "audio/mp3", "audio/wav", "audio/x-wav"
    );

    /** Copy buffer size used while persisting uploaded files. */
    private static final int COPY_BUFFER_SIZE = 8192;

    /** Repository for track persistence and lookup operations. */
    private final TrackRepository trackRepository;

    /** Repository for artist lookups. */
    private final ArtistRepository artistRepository;

    private final UserRepository     userRepository;

    /** Producer for track import events. */
    private final TrackEventProducer trackEventProducer;

    /** Producer for track played events. */
    private final TrackPlayedProducer trackPlayedProducer;

    /** Root path where uploaded audio files are stored. */
    @Value("${audio.storage.path:/app/audio}")
    private String audioStoragePath;

    /**
     * Uploads an audio track and stores its metadata.
     *
     * @param file uploaded audio file
     * @param request upload metadata payload
     * @param uploadedByEmail user email that performed the upload
     * @return created track DTO
     * @throws IOException if file persistence fails
     */
    public TrackDto uploadTrack(final MultipartFile file,
                                final TrackUploadRequest request,
                                final String uploadedByEmail)
            throws IOException {
        validateAudioFile(file);

        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist not found: " + request.getArtistId()));

        String originalName = file.getOriginalFilename();
        String filename = UUID.randomUUID() + getExtension(originalName);
        Path storage = Path.of(audioStoragePath);
        Files.createDirectories(storage);

        try (InputStream in = file.getInputStream();
             OutputStream out =
                     Files.newOutputStream(storage.resolve(filename))) {
            byte[] buffer = new byte[COPY_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        User uploader = userRepository.findByEmail(uploadedByEmail).orElse(null);

        Track track = Track.builder()
                .title(request.getTitle())
                .duration(request.getDuration())
                .genre(request.getGenre())
                .mood(request.getMood())
                .filePath(filename)
                .artist(artist)
                .createdBy(uploader)
                .build();

        Track saved = trackRepository.save(track);
        log.info(
                "Track sauvegarde : id={}, fichier={}",
                saved.getId(),
                filename
        );

        trackEventProducer.sendTrackImported(TrackImportedEvent.builder()
                .trackId(saved.getId())
                .filePath(filename)
                .title(saved.getTitle())
                .uploadedByEmail(uploadedByEmail)
                .build());

        return toDto(saved);
    }

    /**
     * Streams a track resource with optional HTTP range support.
     *
     * @param trackId track identifier
     * @param requestHeaders incoming request headers
     * @return response containing full or partial resource stream
     * @throws MalformedURLException if the file URL cannot be created
     */
    public ResponseEntity<Resource> streamTrack(
            final Long trackId,
            final HttpHeaders requestHeaders)
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
        } catch (IOException ex) {
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

        HttpRange range = requestHeaders.getRange().get(0);
        long start = range.getRangeStart(fileLength);
        long end = range.getRangeEnd(fileLength);
        long contentLength = end - start + 1;

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(mediaType)
                .contentLength(contentLength)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(
                        HttpHeaders.CONTENT_RANGE,
                        "bytes " + start + "-" + end + "/" + fileLength
                )
                .body(new RangeResource(resource, start, contentLength));
    }

    /**
     * Publishes a track-played event to Kafka.
     *
     * @param trackId track identifier
     * @param userEmail listener email
     * @param durationListened listened duration in seconds
     */
    public void publishTrackPlayed(final Long trackId,
                                   final String userEmail,
                                   final Integer durationListened) {
        if (!trackRepository.existsById(trackId)) {
            throw new ResourceNotFoundException("Track not found: " + trackId);
        }

        trackPlayedProducer.sendTrackPlayed(TrackPlayedEvent.builder()
                .trackId(trackId)
                .userEmail(userEmail)
                .durationListened(durationListened)
                .build());
    }

    /**
     * Maps a track entity to DTO.
     *
     * @param track source entity
     * @return mapped DTO
     */
    private TrackDto toDto(final Track track) {
        return new TrackDto(
                track.getId(),
                track.getTitle(),
                track.getDuration(),
                track.getGenre(),
                track.getMood(),
                track.getFilePath(),
                track.getAlbum()    != null ? track.getAlbum().getId()        : null,
                track.getArtist()    != null ? track.getArtist().getId()         : null,
                track.getCreatedBy() != null ? track.getCreatedBy().getEmail()  : null
        );
    }

    /**
     * Validates uploaded audio file constraints.
     *
     * @param file uploaded file
     */
    private void validateAudioFile(final MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Format non supporté. Seuls MP3 et WAV sont acceptés. "
                            + "Recu : " + contentType
            );
        }
    }

    /**
     * Resolves the file extension from the original filename.
     *
     * @param filename original filename
     * @return extension starting with a dot
     */
    private String getExtension(final String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".mp3";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
