package com.RentFlow.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.SubscriptionPlanRequestDTO;
import com.RentFlow.dto.response.SubscriptionPlanResponseDTO;
import com.RentFlow.entity.SubscriptionPlan;
import com.RentFlow.exception.BadRequestException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.SubscriptionPlanRepository;
import com.RentFlow.service.SubscriptionPlanService;

@Service
@Transactional
public class SubscriptionPlanServiceImpl
        implements SubscriptionPlanService {

    private final SubscriptionPlanRepository
            subscriptionPlanRepository;

    public SubscriptionPlanServiceImpl(
            SubscriptionPlanRepository subscriptionPlanRepository) {

        this.subscriptionPlanRepository =
                subscriptionPlanRepository;
    }

    @Override
    public SubscriptionPlanResponseDTO createPlan(
            SubscriptionPlanRequestDTO request) {

        if (subscriptionPlanRepository
                .existsByName(request.getName())) {

            throw new BadRequestException(
                    "Subscription plan already exists");
        }

        SubscriptionPlan plan =
                new SubscriptionPlan();

        plan.setName(request.getName());
        plan.setPrice(request.getPrice());
        plan.setBillingCycle(request.getBillingCycle());
        plan.setMaxProperties(request.getMaxProperties());
        plan.setMaxTenants(request.getMaxTenants());
        plan.setMaxUsers(request.getMaxUsers());
        plan.setActive(true);

        SubscriptionPlan savedPlan =
                subscriptionPlanRepository.save(plan);

        return mapToResponse(savedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionPlanResponseDTO getPlanById(
            Long id) {

        SubscriptionPlan plan =
                subscriptionPlanRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription plan not found with id: "
                                                + id));

        return mapToResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponseDTO>
            getAllActivePlans() {

        return subscriptionPlanRepository
                .findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponseDTO>
            getAllPlans() {

        return subscriptionPlanRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlanResponseDTO updatePlan(
            Long id,
            SubscriptionPlanRequestDTO request) {

        SubscriptionPlan plan =
                subscriptionPlanRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Subscription plan not found with id: "
                                                + id));

        /*
         * Check whether another plan already uses
         * the requested name.
         */
        if (!plan.getName().equals(request.getName())
                && subscriptionPlanRepository
                        .existsByName(request.getName())) {

            throw new RuntimeException(
                    "Subscription plan name already exists");
        }

        plan.setName(request.getName());
        plan.setPrice(request.getPrice());
        plan.setBillingCycle(request.getBillingCycle());
        plan.setMaxProperties(request.getMaxProperties());
        plan.setMaxTenants(request.getMaxTenants());
        plan.setMaxUsers(request.getMaxUsers());

        SubscriptionPlan updatedPlan =
                subscriptionPlanRepository.save(plan);

        return mapToResponse(updatedPlan);
    }

    @Override
    public void deactivatePlan(Long id) {

        SubscriptionPlan plan =
                subscriptionPlanRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Subscription plan not found with id: "
                                                + id));

        plan.setActive(false);

        subscriptionPlanRepository.save(plan);
    }

    private SubscriptionPlanResponseDTO mapToResponse(
            SubscriptionPlan plan) {

        return new SubscriptionPlanResponseDTO(
                plan.getId(),
                plan.getName(),
                plan.getPrice(),
                plan.getBillingCycle(),
                plan.getMaxProperties(),
                plan.getMaxTenants(),
                plan.getMaxUsers(),
                plan.getActive()
        );
    }
}