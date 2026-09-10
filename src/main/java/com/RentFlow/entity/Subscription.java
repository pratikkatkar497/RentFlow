package com.RentFlow.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Index;

import com.RentFlow.enums.SubscriptionStatus;

@Entity
@Table(
    name = "subscriptions",
    indexes = {
        @Index(
            name = "idx_subscription_org",
            columnList = "organization_id"
        ),
        @Index(
            name = "idx_subscription_status",
            columnList = "status"
        ),
        @Index(
            name = "idx_subscription_plan",
            columnList = "plan_id"
        )
    }
)
public class Subscription extends BaseEntity {

    /*
     * One organization has one current subscription.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "organization_id",
        nullable = false,
        unique = true
    )
    private Organization organization;

    /*
     * Subscription plan comes from subscription_plans table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "plan_id",
        nullable = false
    )
    private SubscriptionPlan plan;

    /*
     * Current subscription status.
     */
    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 20
    )
    private SubscriptionStatus status;

    /*
     * Price paid / applicable for this subscription.
     */
    @Column(
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal price;

    /*
     * Subscription start date.
     */
    @Column(
        name = "start_date",
        nullable = false
    )
    private LocalDateTime startDate;

    /*
     * Subscription end date.
     */
    @Column(
        name = "end_date",
        nullable = false
    )
    private LocalDateTime endDate;

    /*
     * Next renewal date.
     */
    @Column(
        name = "renewal_date"
    )
    private LocalDate renewalDate;

    /*
     * Whether subscription renews automatically.
     */
    @Column(
        name = "auto_renew",
        nullable = false
    )
    private Boolean autoRenew = false;

    /*
     * External subscription ID from payment provider.
     */
    @Column(
        name = "external_subscription_id",
        length = 255
    )
    private String externalSubscriptionId;

    /*
     * Payment provider name.
     * Example: RAZORPAY, STRIPE
     */
    @Column(
        name = "payment_provider",
        length = 50
    )
    private String paymentProvider;

    public Subscription() {
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(LocalDate renewalDate) {
        this.renewalDate = renewalDate;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public String getExternalSubscriptionId() {
        return externalSubscriptionId;
    }

    public void setExternalSubscriptionId(String externalSubscriptionId) {
        this.externalSubscriptionId = externalSubscriptionId;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }
}