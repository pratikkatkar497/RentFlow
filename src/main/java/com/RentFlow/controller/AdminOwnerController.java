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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.dto.request.CreateOwnerRequestDTO;
import com.RentFlow.dto.request.UpdateOwnerRequestDTO;
import com.RentFlow.dto.response.AdminUserResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.AdminOwnerService;

@RestController
@RequestMapping("/api/admin/owners")
public class AdminOwnerController {

    private final AdminOwnerService adminOwnerService;

    public AdminOwnerController(
            AdminOwnerService adminOwnerService) {
        this.adminOwnerService = adminOwnerService;
    }

    // Create Owner
    @PostMapping
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>>
            createOwner(
                    @Valid @RequestBody CreateOwnerRequestDTO request) {

        AdminUserResponseDTO owner =
                adminOwnerService.createOwner(request);

        ApiResponse<AdminUserResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Owner created successfully",
                        owner
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get All Owners
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminUserResponseDTO>>>
            getAllOwners() {

        List<AdminUserResponseDTO> owners =
                adminOwnerService.getAllOwners();

        ApiResponse<List<AdminUserResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Owners fetched successfully",
                        owners
                );

        return ResponseEntity.ok(response);
    }

    // Get Owner By ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>>
            getOwnerById(
                    @PathVariable Long id) {

        AdminUserResponseDTO owner =
                adminOwnerService.getOwnerById(id);

        ApiResponse<AdminUserResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Owner fetched successfully",
                        owner
                );

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>>
            updateOwnerStatus(
                    @PathVariable Long id,
                    @RequestParam boolean enabled) {

        AdminUserResponseDTO owner =
                adminOwnerService.updateOwnerStatus(
                        id,
                        enabled);

        String message = enabled
                ? "Owner enabled successfully"
                : "Owner disabled successfully";

        ApiResponse<AdminUserResponseDTO> response =
                new ApiResponse<>(
                        true,
                        message,
                        owner
                );

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>>
            updateOwner(
                    @PathVariable Long id,
                    @Valid @RequestBody UpdateOwnerRequestDTO request) {

        AdminUserResponseDTO owner =
                adminOwnerService.updateOwner(id, request);

        ApiResponse<AdminUserResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Owner updated successfully",
                        owner
                );

        return ResponseEntity.ok(response);
    }
}