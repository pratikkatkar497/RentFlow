package com.RentFlow.service;

public interface SubscriptionLimitService {

	void checkSubscriptionActive(Long organizationId);
	
    void checkPropertyLimit(Long organizationId);

    void checkTenantLimit(Long organizationId);

    void checkUserLimit(Long organizationId);
}