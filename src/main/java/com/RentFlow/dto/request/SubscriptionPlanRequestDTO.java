package com.RentFlow.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.RentFlow.enums.BillingCycle;

public class SubscriptionPlanRequestDTO {

    @NotBlank(message = "Plan name is required")
    @Size(max = 50, message = "Plan name cannot exceed 50 characters")
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "Price cannot be negative"
    )
    private BigDecimal price;

    @NotNull(message = "Billing cycle is required")
    private BillingCycle billingCycle;

    @NotNull(message = "Maximum properties is required")
    @Min(value = 1, message = "Maximum properties must be at least 1")
    private Integer maxProperties;

    @NotNull(message = "Maximum tenants is required")
    @Min(value = 1, message = "Maximum tenants must be at least 1")
    private Integer maxTenants;

    @NotNull(message = "Maximum users is required")
    @Min(value = 1, message = "Maximum users must be at least 1")
    private Integer maxUsers;

    public SubscriptionPlanRequestDTO() {
    }

    public SubscriptionPlanRequestDTO(
            String name,
            BigDecimal price,
            BillingCycle billingCycle,
            Integer maxProperties,
            Integer maxTenants,
            Integer maxUsers) {

        this.name = name;
        this.price = price;
        this.billingCycle = billingCycle;
        this.maxProperties = maxProperties;
        this.maxTenants = maxTenants;
        this.maxUsers = maxUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Integer getMaxProperties() {
        return maxProperties;
    }

    public void setMaxProperties(Integer maxProperties) {
        this.maxProperties = maxProperties;
    }

    public Integer getMaxTenants() {
        return maxTenants;
    }

    public void setMaxTenants(Integer maxTenants) {
        this.maxTenants = maxTenants;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }
}