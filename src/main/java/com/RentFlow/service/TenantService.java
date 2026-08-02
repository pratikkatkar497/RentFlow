package com.RentFlow.service;

import java.util.List;

import com.RentFlow.dto.request.CreateTenantRequestDTO;
import com.RentFlow.dto.request.UpdateTenantRequestDTO;
import com.RentFlow.dto.response.TenantResponseDTO;

public interface TenantService {

    TenantResponseDTO createTenant(CreateTenantRequestDTO request);

    List<TenantResponseDTO> getAllTenants();

    TenantResponseDTO getTenantById(Long tenantId);

    TenantResponseDTO updateTenant(
            Long tenantId,
            UpdateTenantRequestDTO request);

    void deactivateTenant(Long tenantId);

}