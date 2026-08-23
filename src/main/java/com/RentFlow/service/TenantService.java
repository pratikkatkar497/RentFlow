package com.RentFlow.service;

import com.RentFlow.dto.request.CreateTenantRequestDTO;
import com.RentFlow.dto.request.TenantFilterRequestDTO;
import com.RentFlow.dto.request.UpdateMyTenantRequestDTO;
import com.RentFlow.dto.request.UpdateTenantRequestDTO;
import com.RentFlow.dto.response.PageResponseDTO;
import com.RentFlow.dto.response.PropertyResponseDTO;
import com.RentFlow.dto.response.TenantResponseDTO;
import com.RentFlow.enums.TenantStatus;

public interface TenantService {

    TenantResponseDTO createTenant(
            CreateTenantRequestDTO request);

    PageResponseDTO<TenantResponseDTO> getAllTenants(
            int page,
            int size,
            String sortBy,
            String direction,
            TenantStatus status);
    PageResponseDTO<TenantResponseDTO> filterTenants(
            TenantFilterRequestDTO request,
            int page,
            int size,
            String sortBy,
            String direction);
    
    TenantResponseDTO getTenantById(
            Long tenantId);

    TenantResponseDTO updateTenant(
            Long tenantId,
            UpdateTenantRequestDTO request);

    void deactivateTenant(
            Long tenantId);

    TenantResponseDTO getMyProfile();

    PropertyResponseDTO getMyProperty();

    TenantResponseDTO updateMyProfile(
            UpdateMyTenantRequestDTO request);
}