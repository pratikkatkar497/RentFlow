package com.RentFlow.dto.response;

import java.math.BigDecimal;

import com.RentFlow.enums.BillingCycle;

public class SubscriptionPlanResponseDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private BillingCycle billingCycle;
    private Integer maxProperties;
    private Integer maxTenants;
    private Integer maxUsers;
    private Boolean active;

    public SubscriptionPlanResponseDTO() {
    }

    public SubscriptionPlanResponseDTO(
            Long id,
            String name,
            BigDecimal price,
            BillingCycle billingCycle,
            Integer maxProperties,
            Integer maxTenants,
            Integer maxUsers,
            Boolean active) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.billingCycle = billingCycle;
        this.maxProperties = maxProperties;
        this.maxTenants = maxTenants;
        this.maxUsers = maxUsers;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}