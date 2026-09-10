
package com.RentFlow.dto.response;

public class AdminDashboardResponseDTO {

    private long totalOrganizations;
    private long activeOrganizations;

    private long totalUsers;
    private long totalOwners;
    private long totalManagers;
    private long totalTenants;

    private long totalProperties;

    private long totalLeases;
    private long activeLeases;
    private long terminatedLeases;
    private long expiredLeases;

    private long totalPayments;
    private long paidPayments;
    private long pendingPayments;
    private long overduePayments;

    public AdminDashboardResponseDTO() {
    }

    public long getTotalOrganizations() {
        return totalOrganizations;
    }

    public void setTotalOrganizations(long totalOrganizations) {
        this.totalOrganizations = totalOrganizations;
    }

    public long getActiveOrganizations() {
        return activeOrganizations;
    }

    public void setActiveOrganizations(long activeOrganizations) {
        this.activeOrganizations = activeOrganizations;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalOwners() {
        return totalOwners;
    }

    public void setTotalOwners(long totalOwners) {
        this.totalOwners = totalOwners;
    }

    public long getTotalManagers() {
        return totalManagers;
    }

    public void setTotalManagers(long totalManagers) {
        this.totalManagers = totalManagers;
    }

    public long getTotalTenants() {
        return totalTenants;
    }

    public void setTotalTenants(long totalTenants) {
        this.totalTenants = totalTenants;
    }

    public long getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(long totalProperties) {
        this.totalProperties = totalProperties;
    }

    public long getTotalLeases() {
        return totalLeases;
    }

    public void setTotalLeases(long totalLeases) {
        this.totalLeases = totalLeases;
    }

    public long getActiveLeases() {
        return activeLeases;
    }

    public void setActiveLeases(long activeLeases) {
        this.activeLeases = activeLeases;
    }

    public long getTerminatedLeases() {
        return terminatedLeases;
    }

    public void setTerminatedLeases(long terminatedLeases) {
        this.terminatedLeases = terminatedLeases;
    }

    public long getExpiredLeases() {
        return expiredLeases;
    }

    public void setExpiredLeases(long expiredLeases) {
        this.expiredLeases = expiredLeases;
    }

    public long getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(long totalPayments) {
        this.totalPayments = totalPayments;
    }

    public long getPaidPayments() {
        return paidPayments;
    }

    public void setPaidPayments(long paidPayments) {
        this.paidPayments = paidPayments;
    }

    public long getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(long pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

    public long getOverduePayments() {
        return overduePayments;
    }

    public void setOverduePayments(long overduePayments) {
        this.overduePayments = overduePayments;
    }
}

