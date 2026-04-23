package com.sonora.sonora.controller;

import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.dto.TrackPlayedRequest;
import com.sonora.sonora.dto.TrackUploadRequest;
import com.sonora.sonora.service.StreamingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

/** REST controller for upload and streaming operations. */
@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
@Tag(name = "Streaming", description = "Upload and stream tracks")
@SecurityRequirement(name = "bearerAuth")
public class StreamingController {

    /** Streaming service. */
    private final StreamingService streamingService;

    /**
     * Uploads a new track file with metadata.
     *
     * @param file audio file
     * @param request metadata payload
     * @param email authenticated user email
     * @return uploaded track
     * @throws IOException if upload persistence fails
     */
        @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
        )
    @Operation(summary = "Upload a track")
    public ResponseEntity<TrackDto> upload(
            @RequestPart("file") final MultipartFile file,
            @RequestPart("metadata") final TrackUploadRequest request,
            @AuthenticationPrincipal final String email) throws IOException {
        TrackDto response = streamingService.uploadTrack(file, request, email);
        return ResponseEntity.ok(response);
    }

    /**
     * Streams track media using optional range headers.
     *
     * @param id track identifier
     * @param headers HTTP headers
     * @return streaming response
     * @throws MalformedURLException if storage URL cannot be created
     */
    @GetMapping("/{id}/stream")
    @Operation(summary = "Stream a track")
    public ResponseEntity<Resource> stream(
            @PathVariable final Long id,
            @RequestHeader final HttpHeaders headers)
            throws MalformedURLException {
        return streamingService.streamTrack(id, headers);
    }

    /**
     * Marks a track as played for the authenticated user.
     *
     * @param id track identifier
     * @param request played payload
     * @param email authenticated user email
     * @return accepted response
     */
    @PostMapping("/{id}/played")
    @Operation(summary = "Publish track played event")
    public ResponseEntity<Void> markPlayed(
            @PathVariable final Long id,
            @Valid @RequestBody final TrackPlayedRequest request,
            @AuthenticationPrincipal final String email) {
        streamingService.publishTrackPlayed(
                id,
                email,
                request.durationListened()
        );
        return ResponseEntity.accepted().build();
    }
}
