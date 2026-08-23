package com.RentFlow.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

public class GeneratePaymentRequestDTO {

    @NotNull(message = "Lease Id is required")
    private Long leaseId;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    public GeneratePaymentRequestDTO() {
    }

    public Long getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(Long leaseId) {
        this.leaseId = leaseId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}