package com.RentFlow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.response.DashboardResponseDTO;
import com.RentFlow.dto.response.TenantDashboardResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService =
                dashboardService;
    }

    // =========================================================
    // OWNER DASHBOARD
    // =========================================================

    @GetMapping("/owner")
    public ResponseEntity<
            ApiResponse<DashboardResponseDTO>>
            getOwnerDashboard() {

        DashboardResponseDTO response =
                dashboardService
                        .getOwnerDashboard();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // =========================================================
    // TENANT DASHBOARD
    // =========================================================

    @GetMapping("/tenant")
    public ResponseEntity<
            ApiResponse<TenantDashboardResponseDTO>>
            getTenantDashboard() {

        TenantDashboardResponseDTO response =
                dashboardService
                        .getTenantDashboard();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }
}