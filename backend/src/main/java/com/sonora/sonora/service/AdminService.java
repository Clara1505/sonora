package com.sonora.sonora.service;

import com.sonora.sonora.dto.AdminStatsDto;
import com.sonora.sonora.dto.UserDto;
import com.sonora.sonora.model.enums.Role;
import com.sonora.sonora.repository.PlaylistRepository;
import com.sonora.sonora.repository.TrackRepository;
import com.sonora.sonora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findByRole(Role.USER).stream()
                .map(u -> new UserDto(u.getId(), u.getEmail(), u.getRole().name(), u.getCreatedAt()))
                .toList();
    }

    public AdminStatsDto getStats() {
        long userCount = userRepository.countByRole(Role.USER);
        long trackCount = trackRepository.count();
        long playlistCount = playlistRepository.count();
        return new AdminStatsDto(userCount, trackCount, playlistCount);
    }

    @Transactional
    public void deleteUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot delete an admin user");
        }
        userRepository.delete(user);
    }
}
