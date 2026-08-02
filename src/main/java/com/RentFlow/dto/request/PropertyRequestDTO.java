package com.RentFlow.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.RentFlow.enums.PropertyType;

public class PropertyRequestDTO {

    @NotBlank(message = "Property name is required")
    private String propertyName;

    private String description;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotNull(message = "Monthly rent is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Monthly rent must be greater than 0")
    private BigDecimal monthlyRent;

    @NotNull(message = "Security deposit is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Security deposit cannot be negative")
    private BigDecimal securityDeposit;

    @DecimalMin(value = "0.0", inclusive = true,
            message = "Maintenance charge cannot be negative")
    private BigDecimal maintenanceCharge;

    @Positive(message = "Bedrooms must be greater than 0")
    private Integer bedrooms;

    @Positive(message = "Bathrooms must be greater than 0")
    private Integer bathrooms;

    @Positive(message = "Area must be greater than 0")
    private Double area;

    private Boolean parkingAvailable;

    private Boolean balconyAvailable;

    public PropertyRequestDTO() {
    }

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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

	public BigDecimal getMaintenanceCharge() {
		return maintenanceCharge;
	}

	public void setMaintenanceCharge(BigDecimal maintenanceCharge) {
		this.maintenanceCharge = maintenanceCharge;
	}

	public Integer getBedrooms() {
		return bedrooms;
	}

	public void setBedrooms(Integer bedrooms) {
		this.bedrooms = bedrooms;
	}

	public Integer getBathrooms() {
		return bathrooms;
	}

	public void setBathrooms(Integer bathrooms) {
		this.bathrooms = bathrooms;
	}

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	public Boolean getParkingAvailable() {
		return parkingAvailable;
	}

	public void setParkingAvailable(Boolean parkingAvailable) {
		this.parkingAvailable = parkingAvailable;
	}

	public Boolean getBalconyAvailable() {
		return balconyAvailable;
	}

	public void setBalconyAvailable(Boolean balconyAvailable) {
		this.balconyAvailable = balconyAvailable;
	}

  
}