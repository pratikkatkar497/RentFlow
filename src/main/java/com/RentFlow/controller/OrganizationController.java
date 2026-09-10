package com.RentFlow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import com.RentFlow.dto.request.OrganizationRequestDTO;
import com.RentFlow.dto.response.OrganizationResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.OrganizationService;

@RestController
@RequestMapping("/api/admin/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(
            OrganizationService organizationService) {

        this.organizationService = organizationService;
    }

    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<OrganizationResponseDTO>>
            createOrganization(
                    @Valid @RequestBody
                    OrganizationRequestDTO request) {

        OrganizationResponseDTO data =
                organizationService.createOrganization(request);

        ApiResponse<OrganizationResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Organization created successfully",
                        data
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // GET ALL
    // ACTIVE + INACTIVE
    // =========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<OrganizationResponseDTO>>>
            getAllOrganizations() {

        List<OrganizationResponseDTO> data =
                organizationService.getAllOrganizations();

        ApiResponse<List<OrganizationResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Organizations fetched successfully",
                        data
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<OrganizationResponseDTO>>
            getOrganizationById(
                    @PathVariable Long id) {

        OrganizationResponseDTO data =
                organizationService.getOrganizationById(id);

        ApiResponse<OrganizationResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Organization fetched successfully",
                        data
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<OrganizationResponseDTO>>
            updateOrganization(
                    @PathVariable Long id,
                    @Valid @RequestBody
                    OrganizationRequestDTO request) {

        OrganizationResponseDTO data =
                organizationService.updateOrganization(
                        id,
                        request
                );

        ApiResponse<OrganizationResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Organization updated successfully",
                        data
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // DEACTIVATE
    // =========================================================

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>>
            deactivateOrganization(
                    @PathVariable Long id) {

        organizationService.deactivateOrganization(id);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Organization deactivated successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // ACTIVATE
    // =========================================================

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<String>>
            activateOrganization(
                    @PathVariable Long id) {

        organizationService.activateOrganization(id);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Organization activated successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{organizationId}/owner/{ownerId}")
    public ResponseEntity<ApiResponse<String>>
            assignOwner(
                    @PathVariable Long organizationId,
                    @PathVariable Long ownerId) {

        organizationService.assignOwner(
                organizationId,
                ownerId);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Owner assigned successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }
    
}