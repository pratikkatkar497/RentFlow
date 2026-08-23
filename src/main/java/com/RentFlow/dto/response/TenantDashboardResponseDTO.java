package com.RentFlow.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TenantDashboardResponseDTO {

    private Long tenantId;

    private String tenantName;

    private Long propertyId;

    private String propertyName;

    private Long leaseId;

    private LocalDate leaseStartDate;

    private LocalDate leaseEndDate;

    private BigDecimal monthlyRent;

    private LocalDate nextDueDate;

    private BigDecimal nextPaymentAmount;

    private String nextPaymentStatus;

    public TenantDashboardResponseDTO() {
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Long getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(Long leaseId) {
        this.leaseId = leaseId;
    }

    public LocalDate getLeaseStartDate() {
        return leaseStartDate;
    }

    public void setLeaseStartDate(LocalDate leaseStartDate) {
        this.leaseStartDate = leaseStartDate;
    }

    public LocalDate getLeaseEndDate() {
        return leaseEndDate;
    }

    public void setLeaseEndDate(LocalDate leaseEndDate) {
        this.leaseEndDate = leaseEndDate;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public BigDecimal getNextPaymentAmount() {
        return nextPaymentAmount;
    }

    public void setNextPaymentAmount(BigDecimal nextPaymentAmount) {
        this.nextPaymentAmount = nextPaymentAmount;
    }

    public String getNextPaymentStatus() {
        return nextPaymentStatus;
    }

    public void setNextPaymentStatus(String nextPaymentStatus) {
        this.nextPaymentStatus = nextPaymentStatus;
    }
}