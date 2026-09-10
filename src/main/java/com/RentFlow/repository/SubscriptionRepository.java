package com.RentFlow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RentFlow.entity.Organization;
import com.RentFlow.entity.Subscription;
import com.RentFlow.enums.SubscriptionStatus;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	
	Optional<Subscription> findByOrganizationIdAndStatus(
	        Long organizationId,
	        SubscriptionStatus status);

    Optional<Subscription> findByOrganization(Organization organization);

    Optional<Subscription> findByOrganizationId(Long organizationId);

    List<Subscription> findAllByOrderByIdDesc();

    List<Subscription> findByStatusOrderByIdDesc(SubscriptionStatus status);

    long countByStatus(SubscriptionStatus status);

    boolean existsByOrganizationId(Long organizationId);
}