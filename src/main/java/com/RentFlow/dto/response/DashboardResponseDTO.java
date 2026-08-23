package com.RentFlow.dto.response;

import java.math.BigDecimal;

public class DashboardResponseDTO {

	private long totalProperties;

	private long availableProperties;

	private long occupiedProperties;

	private long totalTenants;

	private long activeLeases;
	private double occupancyRate;
	
	private BigDecimal activeLeaseRent;
	private BigDecimal totalMonthlyRent;

	private BigDecimal paidAmount;

	private BigDecimal pendingAmount;

	private BigDecimal overdueAmount;

	public DashboardResponseDTO() {
	}

	public long getTotalProperties() {
		return totalProperties;
	}

	public double getOccupancyRate() {
		return occupancyRate;
	}

	public void setOccupancyRate(double occupancyRate) {
		this.occupancyRate = occupancyRate;
	}

	public BigDecimal getActiveLeaseRent() {
		return activeLeaseRent;
	}

	public void setActiveLeaseRent(BigDecimal activeLeaseRent) {
		this.activeLeaseRent = activeLeaseRent;
	}

	public void setTotalProperties(long totalProperties) {
		this.totalProperties = totalProperties;
	}

	public long getAvailableProperties() {
		return availableProperties;
	}

	public void setAvailableProperties(long availableProperties) {
		this.availableProperties = availableProperties;
	}

	public long getOccupiedProperties() {
		return occupiedProperties;
	}

	public void setOccupiedProperties(long occupiedProperties) {
		this.occupiedProperties = occupiedProperties;
	}

	public long getTotalTenants() {
		return totalTenants;
	}

	public void setTotalTenants(long totalTenants) {
		this.totalTenants = totalTenants;
	}

	public long getActiveLeases() {
		return activeLeases;
	}

	public void setActiveLeases(long activeLeases) {
		this.activeLeases = activeLeases;
	}

	public BigDecimal getTotalMonthlyRent() {
		return totalMonthlyRent;
	}

	public void setTotalMonthlyRent(BigDecimal totalMonthlyRent) {
		this.totalMonthlyRent = totalMonthlyRent;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getPendingAmount() {
		return pendingAmount;
	}

	public void setPendingAmount(BigDecimal pendingAmount) {
		this.pendingAmount = pendingAmount;
	}

	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}
}