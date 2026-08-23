package com.RentFlow.dto.request;

import com.RentFlow.enums.TenantStatus;

public class TenantFilterRequestDTO {

    private String search;

    private TenantStatus status;

    private String occupation;

    private String companyName;

    public TenantFilterRequestDTO() {
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
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
}