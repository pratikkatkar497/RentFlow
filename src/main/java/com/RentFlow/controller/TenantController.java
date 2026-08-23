package com.RentFlow.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.request.CreateTenantRequestDTO;
import com.RentFlow.dto.request.TenantFilterRequestDTO;
import com.RentFlow.dto.request.UpdateMyTenantRequestDTO;
import com.RentFlow.dto.request.UpdateTenantRequestDTO;
import com.RentFlow.dto.response.PageResponseDTO;
import com.RentFlow.dto.response.PropertyResponseDTO;
import com.RentFlow.dto.response.TenantResponseDTO;
import com.RentFlow.enums.TenantStatus;
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
    public ResponseEntity<
            ApiResponse<PageResponseDTO<TenantResponseDTO>>>
            getAllTenants(

                    @RequestParam(
                            defaultValue = "0")
                    int page,

                    @RequestParam(
                            defaultValue = "10")
                    int size,

                    @RequestParam(
                            defaultValue = "id")
                    String sortBy,

                    @RequestParam(
                            defaultValue = "desc")
                    String direction,

                    @RequestParam(
                            required = false)
                    TenantStatus status) {

        PageResponseDTO<TenantResponseDTO> response =
                tenantService.getAllTenants(
                        page,
                        size,
                        sortBy,
                        direction,
                        status);

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
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TenantResponseDTO>> getMyProfile() {

        TenantResponseDTO response =
                tenantService.getMyProfile();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }
    
    @GetMapping("/me/property")
    public ResponseEntity<ApiResponse<PropertyResponseDTO>> getMyProperty() {

        PropertyResponseDTO response =
                tenantService.getMyProperty();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Property details fetched successfully",
                        response
                )
        );
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
    
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<TenantResponseDTO>>
            updateMyProfile(
                    @Valid @RequestBody
                    UpdateMyTenantRequestDTO request) {

        TenantResponseDTO response =
                tenantService.updateMyProfile(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.UPDATED,
                        response));
    }
    
 // =========================================================
 // Search + Filter + Pagination + Sorting
 // =========================================================

 @GetMapping("/filter")
 public ResponseEntity<
         ApiResponse<PageResponseDTO<TenantResponseDTO>>>
         filterTenants(

         @ModelAttribute
         TenantFilterRequestDTO request,

         @RequestParam(
                 defaultValue = "0")
         int page,

         @RequestParam(
                 defaultValue = "10")
         int size,

         @RequestParam(
                 defaultValue = "id")
         String sortBy,

         @RequestParam(
                 defaultValue = "desc")
         String direction) {

     PageResponseDTO<TenantResponseDTO> response =
             tenantService.filterTenants(
                     request,
                     page,
                     size,
                     sortBy,
                     direction);

     return ResponseEntity.ok(
             new ApiResponse<>(
                     true,
                     AppConstants.SUCCESS,
                     response));
 }

}