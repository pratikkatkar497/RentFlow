package com.RentFlow.service.Impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.entity.Subscription;
import com.RentFlow.enums.SubscriptionStatus;
import com.RentFlow.exception.BadRequestException;
import com.RentFlow.exception.SubscriptionLimitExceededException;
import com.RentFlow.repository.PropertyRepository;
import com.RentFlow.repository.SubscriptionRepository;
import com.RentFlow.repository.TenantRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.SubscriptionLimitService;

@Service
@Transactional(readOnly = true)
public class SubscriptionLimitServiceImpl
        implements SubscriptionLimitService {

    private final SubscriptionRepository subscriptionRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    public SubscriptionLimitServiceImpl(
            SubscriptionRepository subscriptionRepository,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            UserRepository userRepository) {

        this.subscriptionRepository = subscriptionRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void checkPropertyLimit(Long organizationId) {

        Subscription subscription =
                getActiveSubscription(organizationId);

        long currentCount =
                propertyRepository
                        .countByOwnerOrganizationId(
                                organizationId);

        Integer maxProperties =
                subscription.getPlan().getMaxProperties();

        if (currentCount >= maxProperties) {
        	throw new SubscriptionLimitExceededException(
        	        "Property limit reached for your subscription plan");
        }
    }

    @Override
    public void checkTenantLimit(Long organizationId) {

        Subscription subscription =
                getActiveSubscription(organizationId);

        long currentCount =
                tenantRepository
                        .countByPropertyOwnerOrganizationId(
                                organizationId);

        Integer maxTenants =
                subscription.getPlan().getMaxTenants();

        if (currentCount >= maxTenants) {
        	throw new SubscriptionLimitExceededException(
        	        "Tenant limit reached for your subscription plan");
        }
    }

    @Override
    public void checkUserLimit(Long organizationId) {

        Subscription subscription =
                getActiveSubscription(organizationId);

        long currentCount =
                userRepository
                        .countByOrganizationId(
                                organizationId);

        Integer maxUsers =
                subscription.getPlan().getMaxUsers();

        if (currentCount >= maxUsers) {
        	throw new SubscriptionLimitExceededException(
        	        "User limit reached for your subscription plan");
        }
    }

    private Subscription getActiveSubscription(
            Long organizationId) {

        return subscriptionRepository
                .findByOrganizationIdAndStatus(
                        organizationId,
                        com.RentFlow.enums.SubscriptionStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "No active subscription found for organization id: "
                                        + organizationId));
    }
    @Override
    public void checkSubscriptionActive(Long organizationId) {

        Subscription subscription =
                subscriptionRepository
                        .findByOrganizationId(organizationId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "No subscription found for organization id: "
                                                + organizationId));

        if (subscription.getStatus()
                != SubscriptionStatus.ACTIVE) {

            throw new BadRequestException(
                    "Your subscription is not active");
        }

        if (subscription.getEndDate() != null
                && subscription.getEndDate()
                        .isBefore(LocalDateTime.now())) {

            throw new BadRequestException(
                    "Your subscription has expired");
        }
    }
    
}