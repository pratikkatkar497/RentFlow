package com.RentFlow.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.TenantStatus;

public interface TenantRepository
extends JpaRepository<Tenant, Long>,
        JpaSpecificationExecutor<Tenant> {
	
	boolean existsByProperty(Property property);

	long countByPropertyOwnerOrganizationId(Long organizationId);
	
    Optional<Tenant> findByEmail(String email);

    Optional<Tenant> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByPropertyAndStatus(
            Property property,
            TenantStatus status);

    // Existing
    java.util.List<Tenant> findByProperty(Property property);

    // PAGINATION
    Page<Tenant> findByPropertyOwner(
            User owner,
            Pageable pageable);

    Optional<Tenant> findByIdAndPropertyOwner(
            Long id,
            User owner);

    // Tenant login profile
    Optional<Tenant> findByUser(User user);
    

    Page<Tenant> findByPropertyOwnerAndStatus(
            User owner,
            TenantStatus status,
            Pageable pageable);
}