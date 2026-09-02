package com.RentFlow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.RentFlow.entity.Lease;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.LeaseStatus;

public interface LeaseRepository
extends JpaRepository<Lease, Long>,
        JpaSpecificationExecutor<Lease> {

    // Find all leases for a property
    List<Lease> findByProperty(Property property);

    // Find all leases for a tenant
    List<Lease> findByTenant(Tenant tenant);

    List<Lease> findByPropertyOwner(User owner);
    
    List<Lease> findByPropertyOwnerAndStatus(
            User owner,
            LeaseStatus status);
    // Check whether a property already has a lease with given status
    boolean existsByPropertyAndStatus(
            Property property,
            LeaseStatus status);

    // Find leases by status
    List<Lease> findByStatus(LeaseStatus status);

    // Find active lease for a property
    List<Lease> findByPropertyAndStatus(
            Property property,
            LeaseStatus status);
    Optional<Lease> findByTenantAndStatus(
            Tenant tenant,
            LeaseStatus status);
	Optional<Lease> findByTenantAndPropertyAndStatus(Tenant tenant, Property property, LeaseStatus status);
	
	  // =========================================================
    // PAGINATION
    // =========================================================

	 // Owner - all leases
    Page<Lease> findByPropertyOwner(
            User owner,
            Pageable pageable);


    // Owner - leases by status
    Page<Lease> findByPropertyOwnerAndStatus(
            User owner,
            LeaseStatus status,
            Pageable pageable);


    // Tenant - all leases
    Page<Lease> findByTenant(
            Tenant tenant,
            Pageable pageable);


    // Tenant - leases by status
    Page<Lease> findByTenantAndStatus(
            Tenant tenant,
            LeaseStatus status,
            Pageable pageable);
	
}