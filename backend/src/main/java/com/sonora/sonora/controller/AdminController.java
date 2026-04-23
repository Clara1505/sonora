package com.sonora.sonora.controller;

import com.sonora.sonora.dto.AdminStatsDto;
import com.sonora.sonora.dto.UserDto;
import com.sonora.sonora.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Controller exposing administrative endpoints for user and stats management.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    /** Service providing admin operations. */
    private final AdminService adminService;

    /**
     * Returns all users.
     *
     * @return list of users
     */
    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return adminService.getAllUsers();
    }

    /**
     * Returns application-wide statistics.
     *
     * @return admin statistics
     */
    @GetMapping("/stats")
    public AdminStatsDto getStats() {
        return adminService.getStats();
    }

    /**
     * Deletes a user by id.
     *
     * @param id user identifier
     */
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable final Long id) {
        adminService.deleteUser(id);
    }
}
