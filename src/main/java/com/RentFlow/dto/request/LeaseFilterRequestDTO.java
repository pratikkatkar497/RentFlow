package com.RentFlow.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.RentFlow.enums.LeaseStatus;

public class LeaseFilterRequestDTO {

    // Search property name or tenant name
    private String search;

    // Filter by property
    private Long propertyId;

    // Filter by tenant
    private Long tenantId;

    // Filter by lease status
    private LeaseStatus status;

    // Rent range
    private BigDecimal minRent;
    private BigDecimal maxRent;

    // Lease start date range
    private LocalDate startDateFrom;
    private LocalDate startDateTo;

    // Lease end date range
    private LocalDate endDateFrom;
    private LocalDate endDateTo;

    public LeaseFilterRequestDTO() {
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public LeaseStatus getStatus() {
        return status;
    }

    public void setStatus(LeaseStatus status) {
        this.status = status;
    }

    public BigDecimal getMinRent() {
        return minRent;
    }

    public void setMinRent(BigDecimal minRent) {
        this.minRent = minRent;
    }

    public BigDecimal getMaxRent() {
        return maxRent;
    }

    public void setMaxRent(BigDecimal maxRent) {
        this.maxRent = maxRent;
    }

    public LocalDate getStartDateFrom() {
        return startDateFrom;
    }

    public void setStartDateFrom(LocalDate startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public LocalDate getStartDateTo() {
        return startDateTo;
    }

    public void setStartDateTo(LocalDate startDateTo) {
        this.startDateTo = startDateTo;
    }

    public LocalDate getEndDateFrom() {
        return endDateFrom;
    }

    public void setEndDateFrom(LocalDate endDateFrom) {
        this.endDateFrom = endDateFrom;
    }

    public LocalDate getEndDateTo() {
        return endDateTo;
    }

    public void setEndDateTo(LocalDate endDateTo) {
        this.endDateTo = endDateTo;
    }
}