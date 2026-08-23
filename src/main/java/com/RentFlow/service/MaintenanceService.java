package com.RentFlow.service;

import java.util.List;

import com.RentFlow.dto.request.CreateMaintenanceRequestDTO;
import com.RentFlow.dto.request.UpdateMaintenanceRequestDTO;
import com.RentFlow.dto.response.MaintenanceResponseDTO;

public interface MaintenanceService {

    // Tenant creates maintenance request
    MaintenanceResponseDTO createMaintenance(
            CreateMaintenanceRequestDTO request);

    // Get all maintenance requests accessible to current user
    List<MaintenanceResponseDTO> getAllMaintenance();

    // Get maintenance request by ID
    MaintenanceResponseDTO getMaintenanceById(Long id);

    // Get maintenance requests by property
    List<MaintenanceResponseDTO> getMaintenanceByProperty(
            Long propertyId);

    // Get maintenance requests by tenant
    List<MaintenanceResponseDTO> getMaintenanceByTenant(
            Long tenantId);

    // Update maintenance details
    MaintenanceResponseDTO updateMaintenance(
            Long id,
            UpdateMaintenanceRequestDTO request);

    // OPEN → IN_PROGRESS
    void startMaintenance(Long id);

    // IN_PROGRESS → RESOLVED
    void resolveMaintenance(Long id);

    // RESOLVED → CLOSED
    void closeMaintenance(Long id);
}