package com.RentFlow.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;

import com.RentFlow.enums.MaintenanceCategory;
import com.RentFlow.enums.MaintenancePriority;

public class UpdateMaintenanceRequestDTO {

    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private MaintenanceCategory category;

    private MaintenancePriority priority;

    private String assignedTo;

    @DecimalMin(value = "0.00", message = "Estimated cost cannot be negative")
    private BigDecimal estimatedCost;

    @DecimalMin(value = "0.00", message = "Actual cost cannot be negative")
    private BigDecimal actualCost;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    public UpdateMaintenanceRequestDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MaintenanceCategory getCategory() {
        return category;
    }

    public void setCategory(MaintenanceCategory category) {
        this.category = category;
    }

    public MaintenancePriority getPriority() {
        return priority;
    }

    public void setPriority(MaintenancePriority priority) {
        this.priority = priority;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}