package com.RentFlow.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.RentFlow.entity.Property;
import com.RentFlow.entity.User;
import com.RentFlow.enums.PropertyStatus;
import com.RentFlow.enums.PropertyType;

public interface PropertyRepository
        extends JpaRepository<Property, Long>,
                JpaSpecificationExecutor<Property> {
	long countByOwnerOrganizationId(Long organizationId);
	
	List<Property> findByOwner(User owner);
	
    Page<Property> findByOwner(
            User owner,
            Pageable pageable);

    Page<Property> findByOwnerAndPropertyNameContainingIgnoreCase(
            User owner,
            String propertyName,
            Pageable pageable);

    Page<Property> findByOwnerAndCityContainingIgnoreCase(
            User owner,
            String city,
            Pageable pageable);

    Page<Property> findByOwnerAndPropertyType(
            User owner,
            PropertyType propertyType,
            Pageable pageable);

    Page<Property> findByOwnerAndStatus(
            User owner,
            PropertyStatus status,
            Pageable pageable);

    Page<Property> findByOwnerAndMonthlyRentBetween(
            User owner,
            BigDecimal minRent,
            BigDecimal maxRent,
            Pageable pageable);

    Optional<Property> findByIdAndOwner(
            Long id,
            User owner);
}