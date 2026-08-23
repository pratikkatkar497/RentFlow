
package com.RentFlow.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

public class UpdateLeaseRequestDTO {

	@NotNull(message = "Monthly rent is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Monthly rent must be greater than 0")
	private BigDecimal monthlyRent;

	@NotNull(message = "Security deposit is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Security deposit must be greater than 0")
	private BigDecimal securityDeposit;

	@NotNull(message = "Start date is required")
	private LocalDate startDate;

	@NotNull(message = "End date is required")
	@Future(message = "End date must be in the future")
	private LocalDate endDate;

	public UpdateLeaseRequestDTO() {
	}

	public BigDecimal getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(BigDecimal monthlyRent) {
		this.monthlyRent = monthlyRent;
	}

	public BigDecimal getSecurityDeposit() {
		return securityDeposit;
	}

	public void setSecurityDeposit(BigDecimal securityDeposit) {
		this.securityDeposit = securityDeposit;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}
