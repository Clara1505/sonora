package com.sonora.sonora.controller;

import com.sonora.sonora.dto.TrackCreateUpdateDto;
import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TrackDto create(
            @Valid @RequestBody TrackCreateUpdateDto dto,
            @AuthenticationPrincipal String email) {
        return trackService.create(dto, email);
    }

    @GetMapping
    public List<TrackDto> getAll() {
        return trackService.getAll();
    }

    @GetMapping("/{id}")
    public TrackDto getById(@PathVariable Long id) {
        return trackService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TrackDto update(@PathVariable Long id, @Valid @RequestBody TrackCreateUpdateDto dto) {
        return trackService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal String email,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        trackService.delete(id, email, isAdmin);
    }
}
