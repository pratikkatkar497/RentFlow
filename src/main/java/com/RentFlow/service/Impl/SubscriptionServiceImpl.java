package com.RentFlow.service.Impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.SubscriptionRequestDTO;
import com.RentFlow.dto.response.SubscriptionDashboardResponseDTO;
import com.RentFlow.dto.response.SubscriptionResponseDTO;
import com.RentFlow.entity.Organization;
import com.RentFlow.entity.Subscription;
import com.RentFlow.entity.SubscriptionPlan;
import com.RentFlow.enums.SubscriptionStatus;
import com.RentFlow.exception.BadRequestException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.OrganizationRepository;
import com.RentFlow.repository.SubscriptionPlanRepository;
import com.RentFlow.repository.SubscriptionRepository;
import com.RentFlow.service.SubscriptionService;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final OrganizationRepository organizationRepository;

    public SubscriptionServiceImpl(
            SubscriptionRepository subscriptionRepository,
            SubscriptionPlanRepository subscriptionPlanRepository,
            OrganizationRepository organizationRepository) {

        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public SubscriptionResponseDTO createSubscription(
            Long organizationId,
            SubscriptionRequestDTO request) {

        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization not found with id: " + organizationId));

        if (subscriptionRepository.existsByOrganizationId(organizationId)) {
            throw new IllegalArgumentException(
                    "Organization already has a subscription");
        }

        SubscriptionPlan plan = subscriptionPlanRepository
                .findById(request.getPlanId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription plan not found with id: "
                                        + request.getPlanId()));

        if (!Boolean.TRUE.equals(plan.getActive())) {
            throw new IllegalArgumentException(
                    "Selected subscription plan is inactive");
        }

        Subscription subscription = new Subscription();

        subscription.setOrganization(organization);
        subscription.setPlan(plan);
        subscription.setPrice(
                plan.getPrice() != null
                        ? plan.getPrice()
                        : BigDecimal.ZERO);

        subscription.setStartDate(LocalDateTime.now());

        subscription.setEndDate(
                calculateEndDate(
                        subscription.getStartDate(),
                        plan));

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        subscription.setAutoRenew(
                Boolean.TRUE.equals(request.getAutoRenew()));

        if (subscription.getAutoRenew()) {
            subscription.setRenewalDate(
                    calculateRenewalDate(
                            subscription.getStartDate(),
                            plan));
        }

        Subscription savedSubscription =
                subscriptionRepository.save(subscription);

        return mapToResponse(savedSubscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponseDTO getSubscriptionById(Long id) {

        Subscription subscription = subscriptionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription not found with id: " + id));

        return mapToResponse(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponseDTO getSubscriptionByOrganizationId(
            Long organizationId) {

        Subscription subscription = subscriptionRepository
                .findByOrganizationId(organizationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription not found for organization id: "
                                        + organizationId));

        return mapToResponse(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getAllSubscriptions() {

        return subscriptionRepository
                .findAllByOrderByIdDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getSubscriptionsByStatus(String status) {

        SubscriptionStatus subscriptionStatus;

        try {
            subscriptionStatus =
                    SubscriptionStatus.valueOf(status.trim().toUpperCase());

        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid subscription status: " + status);
        }

        return subscriptionRepository
                .findByStatusOrderByIdDesc(subscriptionStatus)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionResponseDTO updateSubscription(
            Long id,
            SubscriptionRequestDTO request) {

        Subscription subscription = subscriptionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription not found with id: " + id));

        SubscriptionPlan plan = subscriptionPlanRepository
                .findById(request.getPlanId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription plan not found with id: "
                                        + request.getPlanId()));

        if (!Boolean.TRUE.equals(plan.getActive())) {
            throw new IllegalArgumentException(
                    "Selected subscription plan is inactive");
        }

        subscription.setPlan(plan);

        subscription.setPrice(
                plan.getPrice() != null
                        ? plan.getPrice()
                        : BigDecimal.ZERO);

        subscription.setAutoRenew(
                Boolean.TRUE.equals(request.getAutoRenew()));

        LocalDateTime now = LocalDateTime.now();

        subscription.setStartDate(now);

        subscription.setEndDate(
                calculateEndDate(now, plan));

        if (subscription.getAutoRenew()) {
            subscription.setRenewalDate(
                    calculateRenewalDate(now, plan));
        } else {
            subscription.setRenewalDate(null);
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        Subscription updatedSubscription =
                subscriptionRepository.save(subscription);

        return mapToResponse(updatedSubscription);
    }

    @Override
    public void cancelSubscription(Long id) {

        Subscription subscription = subscriptionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription not found with id: " + id));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);
        subscription.setRenewalDate(null);

        subscriptionRepository.save(subscription);
    }

    @Override
    public void activateSubscription(Long id) {

        Subscription subscription = subscriptionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription not found with id: " + id));

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionDashboardResponseDTO getSubscriptionDashboard(
            Long organizationId) {

        Subscription subscription = subscriptionRepository
                .findByOrganizationId(organizationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription not found for organization id: "
                                        + organizationId));

        SubscriptionDashboardResponseDTO response =
                new SubscriptionDashboardResponseDTO();

        response.setSubscriptionId(subscription.getId());

        response.setOrganizationId(
                subscription.getOrganization().getId());

        response.setOrganizationName(
                subscription.getOrganization().getName());

        response.setPlanId(
                subscription.getPlan().getId());

        response.setPlanName(
                subscription.getPlan().getName());

        response.setPrice(
                subscription.getPrice());

        response.setStatus(
                subscription.getStatus().name());

        response.setStartDate(
                subscription.getStartDate());

        response.setEndDate(
                subscription.getEndDate());

        response.setRenewalDate(
                subscription.getRenewalDate());

        response.setAutoRenew(
                subscription.getAutoRenew());

        return response;
    }

    private LocalDateTime calculateEndDate(
            LocalDateTime startDate,
            SubscriptionPlan plan) {

        if (plan.getBillingCycle() == null) {
            return startDate.plusYears(1);
        }

        switch (plan.getBillingCycle()) {

            case MONTHLY:
                return startDate.plusMonths(1);

            case YEARLY:
                return startDate.plusYears(1);

            default:
                return startDate.plusYears(1);
        }
    }
    
    private LocalDate calculateRenewalDate(
            LocalDateTime startDate,
            SubscriptionPlan plan) {

        if (plan.getBillingCycle() == null) {
            return startDate.toLocalDate().plusYears(1);
        }

        switch (plan.getBillingCycle()) {

            case MONTHLY:
                return startDate.toLocalDate().plusMonths(1);

            case YEARLY:
                return startDate.toLocalDate().plusYears(1);

            default:
                return startDate.toLocalDate().plusYears(1);
        }
    }
    
    private SubscriptionResponseDTO mapToResponse(
            Subscription subscription) {

        SubscriptionResponseDTO response =
                new SubscriptionResponseDTO();

        response.setId(subscription.getId());

        if (subscription.getOrganization() != null) {
            response.setOrganizationId(
                    subscription.getOrganization().getId());

            response.setOrganizationName(
                    subscription.getOrganization().getName());
        }

        if (subscription.getPlan() != null) {
            response.setPlanId(
                    subscription.getPlan().getId());

            response.setPlanName(
                    subscription.getPlan().getName());
        }

        response.setPrice(subscription.getPrice());

        response.setStatus(subscription.getStatus());

        response.setStartDate(
                subscription.getStartDate());

        response.setEndDate(
                subscription.getEndDate());

        response.setRenewalDate(
                subscription.getRenewalDate());

        response.setAutoRenew(
                subscription.getAutoRenew());

        response.setExternalSubscriptionId(
                subscription.getExternalSubscriptionId());

        response.setPaymentProvider(
                subscription.getPaymentProvider());

        response.setCreatedAt(
                subscription.getCreatedAt());

        response.setUpdatedAt(
                subscription.getUpdatedAt());

        return response;
    }
}