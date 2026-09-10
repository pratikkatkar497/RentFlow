package com.RentFlow.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.RentFlow.enums.BillingCycle;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillingCycle billingCycle;

    @Column(nullable = false)
    private Integer maxProperties;

    @Column(nullable = false)
    private Integer maxTenants;

    @Column(nullable = false)
    private Integer maxUsers;

    @Column(nullable = false)
    private Boolean active = true;

    public SubscriptionPlan() {
    }

    public SubscriptionPlan(
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
        this.active = true;
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