package com.RentFlow.service;

import java.util.List;

import com.RentFlow.dto.request.CreateOwnerRequestDTO;
import com.RentFlow.dto.request.UpdateOwnerRequestDTO;
import com.RentFlow.dto.response.AdminUserResponseDTO;

public interface AdminOwnerService {

    AdminUserResponseDTO createOwner(CreateOwnerRequestDTO request);

    List<AdminUserResponseDTO> getAllOwners();

    AdminUserResponseDTO getOwnerById(Long id);
    
    AdminUserResponseDTO updateOwnerStatus(Long id, boolean enabled);
    AdminUserResponseDTO updateOwner(
            Long id,
            UpdateOwnerRequestDTO request
    );
}