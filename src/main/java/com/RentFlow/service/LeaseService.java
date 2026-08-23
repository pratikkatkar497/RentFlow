package com.RentFlow.service;

import com.RentFlow.dto.request.CreateLeaseRequestDTO;
import com.RentFlow.dto.request.UpdateLeaseRequestDTO;
import com.RentFlow.dto.response.LeaseResponseDTO;
import com.RentFlow.dto.response.PageResponseDTO;
import com.RentFlow.enums.LeaseStatus;

public interface LeaseService {

    // Create a new lease
    LeaseResponseDTO createLease(CreateLeaseRequestDTO request);

    // Get all leases belonging to logged-in owner
    PageResponseDTO<LeaseResponseDTO> getAllLeases(
            int page,
            int size,
            String sortBy,
            String direction,
            LeaseStatus status);

    // Get lease by ID
    LeaseResponseDTO getLeaseById(Long leaseId);

    // Get all leases for logged-in owner's properties
    PageResponseDTO<LeaseResponseDTO> getMyLeases(
            int page,
            int size,
            String sortBy,
            String direction);

    // Update lease details
    LeaseResponseDTO updateLease(
            Long leaseId,
            UpdateLeaseRequestDTO request);

    // Activate lease
    LeaseResponseDTO activateLease(Long leaseId);

    // Terminate lease
    LeaseResponseDTO terminateLease(Long leaseId);
    LeaseResponseDTO getMyLease();
}