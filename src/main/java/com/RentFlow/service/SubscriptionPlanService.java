package com.RentFlow.service;

import java.util.List;

import com.RentFlow.dto.request.SubscriptionPlanRequestDTO;
import com.RentFlow.dto.response.SubscriptionPlanResponseDTO;

public interface SubscriptionPlanService {

    SubscriptionPlanResponseDTO createPlan(
            SubscriptionPlanRequestDTO request);

    SubscriptionPlanResponseDTO getPlanById(
            Long id);

    List<SubscriptionPlanResponseDTO> getAllActivePlans();

    List<SubscriptionPlanResponseDTO> getAllPlans();

    SubscriptionPlanResponseDTO updatePlan(
            Long id,
            SubscriptionPlanRequestDTO request);

    void deactivatePlan(Long id);
}