package com.RentFlow.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.request.CreateMaintenanceRequestDTO;
import com.RentFlow.dto.request.UpdateMaintenanceRequestDTO;
import com.RentFlow.dto.response.MaintenanceResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.MaintenanceService;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(
            MaintenanceService maintenanceService) {

        this.maintenanceService = maintenanceService;
    }

    // ---------------------------------------------------------
    // Create Maintenance Request
    // ---------------------------------------------------------

    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceResponseDTO>> createMaintenance(
            @Valid @RequestBody CreateMaintenanceRequestDTO request) {

        MaintenanceResponseDTO response =
                maintenanceService.createMaintenance(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Maintenance request created successfully.",
                        response));
    }

    // ---------------------------------------------------------
    // Get All Maintenance Requests
    // ---------------------------------------------------------

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaintenanceResponseDTO>>> getAllMaintenance() {

        List<MaintenanceResponseDTO> response =
                maintenanceService.getAllMaintenance();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // ---------------------------------------------------------
    // Get Maintenance By ID
    // ---------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceResponseDTO>> getMaintenanceById(
            @PathVariable Long id) {

        MaintenanceResponseDTO response =
                maintenanceService.getMaintenanceById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // ---------------------------------------------------------
    // Get Maintenance By Property
    // ---------------------------------------------------------

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<List<MaintenanceResponseDTO>>> getMaintenanceByProperty(
            @PathVariable Long propertyId) {

        List<MaintenanceResponseDTO> response =
                maintenanceService.getMaintenanceByProperty(
                        propertyId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // ---------------------------------------------------------
    // Get Maintenance By Tenant
    // ---------------------------------------------------------

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<MaintenanceResponseDTO>>> getMaintenanceByTenant(
            @PathVariable Long tenantId) {

        List<MaintenanceResponseDTO> response =
                maintenanceService.getMaintenanceByTenant(
                        tenantId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // ---------------------------------------------------------
    // Update Maintenance
    // ---------------------------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceResponseDTO>> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMaintenanceRequestDTO request) {

        MaintenanceResponseDTO response =
                maintenanceService.updateMaintenance(
                        id,
                        request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.UPDATED,
                        response));
    }

    // ---------------------------------------------------------
    // OPEN → IN_PROGRESS
    // ---------------------------------------------------------

    @PatchMapping("/{id}/start")
    public ResponseEntity<ApiResponse<String>> startMaintenance(
            @PathVariable Long id) {

        maintenanceService.startMaintenance(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Maintenance request started successfully.",
                        null));
    }

    // ---------------------------------------------------------
    // IN_PROGRESS → RESOLVED
    // ---------------------------------------------------------

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<String>> resolveMaintenance(
            @PathVariable Long id) {

        maintenanceService.resolveMaintenance(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Maintenance request resolved successfully.",
                        null));
    }

    // ---------------------------------------------------------
    // RESOLVED → CLOSED
    // ---------------------------------------------------------

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<String>> closeMaintenance(
            @PathVariable Long id) {

        maintenanceService.closeMaintenance(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Maintenance request closed successfully.",
                        null));
    }
}