package com.sonora.sonora.controller;

import com.sonora.sonora.dto.AdminStatsDto;
import com.sonora.sonora.dto.UserDto;
import com.sonora.sonora.service.AdminService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void getUsers_returnsUsersFromService() {
        UserDto user = new UserDto(1L, "u@sonora.com", "USER", LocalDateTime.now());
        when(adminService.getAllUsers()).thenReturn(List.of(user));

        AdminController controller = new AdminController(adminService);
        var res = controller.getUsers();

        assertEquals(1, res.size());
        assertEquals("u@sonora.com", res.get(0).email());
        verify(adminService, times(1)).getAllUsers();
    }

    @Test
    void getStats_returnsStatsFromService() {
        when(adminService.getStats()).thenReturn(new AdminStatsDto(5L, 10L, 2L));

        AdminController controller = new AdminController(adminService);
        var stats = controller.getStats();

        assertEquals(5L, stats.userCount());
        assertEquals(10L, stats.trackCount());
        assertEquals(2L, stats.playlistCount());
        verify(adminService, times(1)).getStats();
    }

    @Test
    void deleteUser_callsService() {
        doNothing().when(adminService).deleteUser(1L);

        AdminController controller = new AdminController(adminService);
        controller.deleteUser(1L);

        verify(adminService, times(1)).deleteUser(1L);
    }
}
