package com.RentFlow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.TenantStatus;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByEmail(String email);

    Optional<Tenant> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByPropertyAndStatus(
            Property property,
            TenantStatus status);

    List<Tenant> findByProperty(Property property);

    List<Tenant> findByPropertyOwner(User owner);

    Optional<Tenant> findByIdAndPropertyOwner(
            Long id,
            User owner);

}