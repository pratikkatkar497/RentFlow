package com.RentFlow.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.request.CreateLeaseRequestDTO;
import com.RentFlow.dto.request.UpdateLeaseRequestDTO;
import com.RentFlow.dto.response.LeaseResponseDTO;
import com.RentFlow.dto.response.PageResponseDTO;
import com.RentFlow.enums.LeaseStatus;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.LeaseService;

@RestController
@RequestMapping("/api/leases")
public class LeaseController {

    private final LeaseService leaseService;

    public LeaseController(
            LeaseService leaseService) {

        this.leaseService = leaseService;
    }


    // =========================================================
    // CREATE LEASE
    // =========================================================

    @PostMapping
    public ResponseEntity<ApiResponse<LeaseResponseDTO>> createLease(
            @Valid @RequestBody CreateLeaseRequestDTO request) {

        LeaseResponseDTO response =
                leaseService.createLease(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                AppConstants.CREATED,
                                response));
    }


    // =========================================================
    // GET ALL OWNER LEASES - PAGINATION
    // =========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<PageResponseDTO<LeaseResponseDTO>>>
            getAllLeases(

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
                    LeaseStatus status) {

        PageResponseDTO<LeaseResponseDTO> response =
                leaseService.getAllLeases(
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

    // =========================================================
    // GET LEASE BY ID
    // =========================================================

    @GetMapping("/{leaseId}")
    public ResponseEntity<
            ApiResponse<LeaseResponseDTO>>
            getLeaseById(
                    @PathVariable Long leaseId) {

        LeaseResponseDTO response =
                leaseService.getLeaseById(
                        leaseId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }


    // =========================================================
    // TENANT - GET MY LEASES - PAGINATION
    // =========================================================

    @GetMapping("/my")
    public ResponseEntity<
            ApiResponse<PageResponseDTO<LeaseResponseDTO>>>
            getMyLeases(

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

        PageResponseDTO<LeaseResponseDTO> response =
                leaseService.getMyLeases(
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


    // =========================================================
    // UPDATE LEASE
    // =========================================================

    @PutMapping("/{leaseId}")
    public ResponseEntity<
            ApiResponse<LeaseResponseDTO>>
            updateLease(

                    @PathVariable Long leaseId,

                    @Valid
                    @RequestBody
                    UpdateLeaseRequestDTO request) {

        LeaseResponseDTO response =
                leaseService.updateLease(
                        leaseId,
                        request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.UPDATED,
                        response));
    }


    // =========================================================
    // ACTIVATE LEASE
    // =========================================================

    @PutMapping("/{leaseId}/activate")
    public ResponseEntity<
            ApiResponse<LeaseResponseDTO>>
            activateLease(
                    @PathVariable Long leaseId) {

        LeaseResponseDTO response =
                leaseService.activateLease(
                        leaseId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.UPDATED,
                        response));
    }


    // =========================================================
    // TERMINATE LEASE
    // =========================================================

    @PutMapping("/{leaseId}/terminate")
    public ResponseEntity<
            ApiResponse<LeaseResponseDTO>>
            terminateLease(
                    @PathVariable Long leaseId) {

        LeaseResponseDTO response =
                leaseService.terminateLease(
                        leaseId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.UPDATED,
                        response));
    }


    // =========================================================
    // TENANT - GET ACTIVE LEASE
    // =========================================================

    @GetMapping("/my/active")
    public ResponseEntity<
            ApiResponse<LeaseResponseDTO>>
            getMyLease() {

        LeaseResponseDTO response =
                leaseService.getMyLease();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }
}