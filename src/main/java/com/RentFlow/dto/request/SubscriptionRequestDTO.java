package com.RentFlow.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SubscriptionRequestDTO {

    @NotNull(message = "Plan ID is required")
    @Positive(message = "Plan ID must be greater than 0")
    private Long planId;

    private Boolean autoRenew = false;

    public SubscriptionRequestDTO() {
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }
}