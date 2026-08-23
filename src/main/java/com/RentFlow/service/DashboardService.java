package com.RentFlow.service;

import com.RentFlow.dto.response.DashboardResponseDTO;
import com.RentFlow.dto.response.TenantDashboardResponseDTO;

public interface DashboardService {

    // Owner / Manager dashboard
    DashboardResponseDTO getOwnerDashboard();

    // Current tenant dashboard
    TenantDashboardResponseDTO getTenantDashboard();
}