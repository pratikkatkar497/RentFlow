package com.RentFlow.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.RentFlow.service.LeaseService;

@Component
public class LeaseScheduler {

    private final LeaseService leaseService;

    public LeaseScheduler(LeaseService leaseService) {
        this.leaseService = leaseService;
    }

    /**
     * Runs every day at midnight.
     *
     * Checks active leases and automatically expires
     * leases whose end date has passed.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateExpiredLeases() {

        leaseService.updateExpiredLeases();
    }

    /**
     * Runs every day at 12:05 AM.
     *
     * Sends lease expiry reminders at:
     * 30 days
     * 7 days
     * 1 day
     */
    @Scheduled(cron = "0 5 0 * * *")
    public void sendLeaseExpiryNotifications() {

        leaseService.sendLeaseExpiryNotifications();
    }
}