package com.RentFlow.dto.request;

import java.math.BigDecimal;

import com.RentFlow.enums.PropertyStatus;
import com.RentFlow.enums.PropertyType;

public class PropertyFilterRequestDTO {

    private String search;

    private String city;

    private String state;

    private PropertyType propertyType;

    private PropertyStatus status;

    private BigDecimal minRent;

    private BigDecimal maxRent;

    // =========================================================
    // Constructors
    // =========================================================

    public PropertyFilterRequestDTO() {
    }

    // =========================================================
    // Getters and Setters
    // =========================================================

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
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
}