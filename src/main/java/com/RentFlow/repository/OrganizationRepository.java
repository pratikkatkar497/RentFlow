package com.RentFlow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RentFlow.entity.Organization;

public interface OrganizationRepository
        extends JpaRepository<Organization, Long> {

    Optional<Organization> findBySlug(String slug);

    List<Organization> findByActiveTrue();

    List<Organization> findAllByOrderByIdDesc();

    boolean existsBySlug(String slug);

    long countByActiveTrue();
}