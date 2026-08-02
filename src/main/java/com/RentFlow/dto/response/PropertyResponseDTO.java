package com.RentFlow.dto.response;

import java.math.BigDecimal;

import com.RentFlow.enums.PropertyStatus;
import com.RentFlow.enums.PropertyType;

public class PropertyResponseDTO {

    private Long id;

    private String propertyName;

    private String description;

    private PropertyType propertyType;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private BigDecimal monthlyRent;

    private BigDecimal securityDeposit;

    private BigDecimal maintenanceCharge;

    private Integer bedrooms;

    private Integer bathrooms;

    private Double area;

    private Boolean parkingAvailable;

    private Boolean balconyAvailable;

    private PropertyStatus status;

    private Long ownerId;

    private String ownerName;

    public PropertyResponseDTO() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public PropertyStatus getStatus() {
		return status;
	}

	public void setStatus(PropertyStatus status) {
		this.status = status;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

    
}