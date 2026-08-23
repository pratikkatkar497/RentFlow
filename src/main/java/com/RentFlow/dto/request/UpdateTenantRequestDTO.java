package com.RentFlow.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UpdateTenantRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(
        max = 50,
        message = "First name cannot exceed 50 characters"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(
        max = 50,
        message = "Last name cannot exceed 50 characters"
    )
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    @Size(
        max = 100,
        message = "Email cannot exceed 100 characters"
    )
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone number must contain exactly 10 digits"
    )
    private String phone;

    @Size(
        max = 20,
        message = "Gender cannot exceed 20 characters"
    )
    private String gender;

    @Size(
        max = 100,
        message = "Occupation cannot exceed 100 characters"
    )
    private String occupation;

    @Size(
        max = 150,
        message = "Company name cannot exceed 150 characters"
    )
    private String companyName;

    @Pattern(
        regexp = "^[0-9]{12}$",
        message = "Aadhaar number must contain exactly 12 digits"
    )
    private String aadhaarNumber;

    @Size(
        max = 500,
        message = "Permanent address cannot exceed 500 characters"
    )
    private String permanentAddress;

    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Emergency contact must contain exactly 10 digits"
    )
    private String emergencyContact;

    @NotNull(message = "Move in date is required")
    private LocalDate moveInDate;

    public UpdateTenantRequestDTO() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }
}

