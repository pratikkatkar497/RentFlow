package com.RentFlow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.RentFlow.dto.request.SubscriptionPlanRequestDTO;
import com.RentFlow.dto.response.SubscriptionPlanResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.SubscriptionPlanService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(
            SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService =
                subscriptionPlanService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>>
            createPlan(
                    @Valid @RequestBody SubscriptionPlanRequestDTO request) {

        SubscriptionPlanResponseDTO plan =
                subscriptionPlanService.createPlan(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Subscription plan created successfully",
                        plan
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>>
            getPlanById(@PathVariable Long id) {

        SubscriptionPlanResponseDTO plan =
                subscriptionPlanService.getPlanById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Subscription plan retrieved successfully",
                        plan
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponseDTO>>>
            getAllPlans() {

        List<SubscriptionPlanResponseDTO> plans =
                subscriptionPlanService.getAllPlans();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Subscription plans retrieved successfully",
                        plans
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponseDTO>>>
            getAllActivePlans() {

        List<SubscriptionPlanResponseDTO> plans =
                subscriptionPlanService.getAllActivePlans();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Active subscription plans retrieved successfully",
                        plans
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>>
            updatePlan(
                    @PathVariable Long id,
                    @Valid @RequestBody SubscriptionPlanRequestDTO request) {

        SubscriptionPlanResponseDTO plan =
                subscriptionPlanService.updatePlan(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Subscription plan updated successfully",
                        plan
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>>
            deactivatePlan(@PathVariable Long id) {

        subscriptionPlanService.deactivatePlan(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Subscription plan deactivated successfully",
                        null
                )
        );
    }
}