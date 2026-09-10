
package com.RentFlow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.response.AdminDashboardResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.AdminDashboardService;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService) {

        this.adminDashboardService =
                adminDashboardService;
    }

    @GetMapping
    public ResponseEntity<
            ApiResponse<AdminDashboardResponseDTO>>
            getAdminDashboard() {

        AdminDashboardResponseDTO response =
                adminDashboardService
                        .getAdminDashboard();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response
                )
        );
    }
}

