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
import com.RentFlow.dto.request.CreateTenantRequestDTO;
import com.RentFlow.dto.request.UpdateTenantRequestDTO;
import com.RentFlow.dto.response.TenantResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.TenantService;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    // Create Tenant
    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponseDTO>> createTenant(
            @Valid @RequestBody CreateTenantRequestDTO request) {

        TenantResponseDTO response = tenantService.createTenant(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        AppConstants.CREATED,
                        response));
    }

    // Get All Tenants
    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantResponseDTO>>> getAllTenants() {

        List<TenantResponseDTO> response =
                tenantService.getAllTenants();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // Get Tenant By Id
    @GetMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResponseDTO>> getTenantById(
            @PathVariable Long tenantId) {

        TenantResponseDTO response =
                tenantService.getTenantById(tenantId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // Update Tenant
    @PutMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResponseDTO>> updateTenant(
            @PathVariable Long tenantId,
            @Valid @RequestBody UpdateTenantRequestDTO request) {

        TenantResponseDTO response =
                tenantService.updateTenant(tenantId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.UPDATED,
                        response));
    }

    // Deactivate Tenant (Soft Delete)
    @PatchMapping("/{tenantId}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateTenant(
            @PathVariable Long tenantId) {

        tenantService.deactivateTenant(tenantId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Tenant deactivated successfully.",
                        null));
    }

}