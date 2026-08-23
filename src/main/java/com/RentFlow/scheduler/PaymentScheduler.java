package com.RentFlow.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.RentFlow.service.PaymentService;

@Component
public class PaymentScheduler {

    private final PaymentService paymentService;

    public PaymentScheduler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateOverduePayments() {

        paymentService.updateOverduePayments();
    }
}