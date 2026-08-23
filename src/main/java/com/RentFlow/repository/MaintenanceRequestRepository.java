package com.RentFlow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RentFlow.entity.MaintenanceRequest;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.enums.MaintenanceStatus;

public interface MaintenanceRequestRepository
        extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findByProperty(Property property);

    List<MaintenanceRequest> findByTenant(Tenant tenant);

    List<MaintenanceRequest> findByStatus(MaintenanceStatus status);
}