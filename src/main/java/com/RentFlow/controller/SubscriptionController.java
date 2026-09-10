package com.RentFlow.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.RentFlow.dto.request.SubscriptionRequestDTO;
import com.RentFlow.dto.response.SubscriptionDashboardResponseDTO;
import com.RentFlow.dto.response.SubscriptionResponseDTO;
import com.RentFlow.service.SubscriptionService;

@RestController
@RequestMapping("/api/admin/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(
            SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/organization/{organizationId}")
    public ResponseEntity<SubscriptionResponseDTO> createSubscription(
            @PathVariable Long organizationId,
            @Valid @RequestBody SubscriptionRequestDTO request) {

        SubscriptionResponseDTO response =
                subscriptionService.createSubscription(
                        organizationId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                subscriptionService.getSubscriptionById(id));
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<SubscriptionResponseDTO>
            getSubscriptionByOrganizationId(
                    @PathVariable Long organizationId) {

        return ResponseEntity.ok(
                subscriptionService
                        .getSubscriptionByOrganizationId(organizationId));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDTO>>
            getAllSubscriptions() {

        return ResponseEntity.ok(
                subscriptionService.getAllSubscriptions());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SubscriptionResponseDTO>>
            getSubscriptionsByStatus(
                    @PathVariable String status) {

        return ResponseEntity.ok(
                subscriptionService
                        .getSubscriptionsByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDTO>
            updateSubscription(
                    @PathVariable Long id,
                    @Valid @RequestBody SubscriptionRequestDTO request) {

        return ResponseEntity.ok(
                subscriptionService
                        .updateSubscription(id, request));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(
            @PathVariable Long id) {

        subscriptionService.cancelSubscription(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateSubscription(
            @PathVariable Long id) {

        subscriptionService.activateSubscription(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/organization/{organizationId}/dashboard")
    public ResponseEntity<SubscriptionDashboardResponseDTO>
            getSubscriptionDashboard(
                    @PathVariable Long organizationId) {

        return ResponseEntity.ok(
                subscriptionService
                        .getSubscriptionDashboard(organizationId));
    }
}