package com.sonora.sonora.controller;
import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.dto.TrackUploadRequest;
import com.sonora.sonora.service.StreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class StreamingController {

    private final StreamingService streamingService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrackDto> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") TrackUploadRequest request,
            @AuthenticationPrincipal String email) throws IOException {
        TrackDto response = streamingService.uploadTrack(file, request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<Resource> stream(
            @PathVariable Long id,
            @RequestHeader HttpHeaders headers) throws MalformedURLException {

        return streamingService.streamTrack(id, headers);
    }
}