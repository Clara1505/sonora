package com.sonora.sonora.controller;

import com.sonora.sonora.dto.TrackCreateUpdateDto;
import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrackDto create(@Valid @RequestBody TrackCreateUpdateDto dto) {
        return trackService.create(dto);
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
    public TrackDto update(@PathVariable Long id, @Valid @RequestBody TrackCreateUpdateDto dto) {
        return trackService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        trackService.delete(id);
    }
}
