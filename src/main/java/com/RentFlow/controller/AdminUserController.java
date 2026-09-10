package com.RentFlow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.dto.response.AdminUserResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.AdminUserService;

import com.RentFlow.constant.AppConstants;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService) {

        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminUserResponseDTO>>>
            getAllUsers() {

        List<AdminUserResponseDTO> users =
                adminUserService.getAllUsers();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        users
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>>
            getUserById(@PathVariable Long id) {

        AdminUserResponseDTO user =
                adminUserService.getUserById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        user
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>>
            updateUserStatus(
                    @PathVariable Long id,
                    @RequestParam boolean enabled) {

        AdminUserResponseDTO user =
                adminUserService.updateUserStatus(
                        id,
                        enabled
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User status updated successfully.",
                        user
                )
        );
    }
}