package com.RentFlow.service;

import java.util.List;

import com.RentFlow.dto.request.SubscriptionRequestDTO;
import com.RentFlow.dto.response.SubscriptionDashboardResponseDTO;
import com.RentFlow.dto.response.SubscriptionResponseDTO;

public interface SubscriptionService {

    SubscriptionResponseDTO createSubscription(
            Long organizationId,
            SubscriptionRequestDTO request);

    SubscriptionResponseDTO getSubscriptionById(Long id);

    SubscriptionResponseDTO getSubscriptionByOrganizationId(
            Long organizationId);

    List<SubscriptionResponseDTO> getAllSubscriptions();

    List<SubscriptionResponseDTO> getSubscriptionsByStatus(
            String status);

    SubscriptionResponseDTO updateSubscription(
            Long id,
            SubscriptionRequestDTO request);

    void cancelSubscription(Long id);

    void activateSubscription(Long id);

    SubscriptionDashboardResponseDTO getSubscriptionDashboard(
            Long organizationId);
}