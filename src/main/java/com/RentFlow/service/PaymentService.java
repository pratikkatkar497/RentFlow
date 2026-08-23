package com.RentFlow.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.RentFlow.dto.request.CreatePaymentRequestDTO;
import com.RentFlow.dto.request.GeneratePaymentRequestDTO;
import com.RentFlow.dto.request.UpdatePaymentRequestDTO;
import com.RentFlow.dto.response.PageResponseDTO;
import com.RentFlow.dto.response.PaymentResponseDTO;
import com.RentFlow.enums.PaymentStatus;

public interface PaymentService {

    // =========================================================
    // Create Payment
    // =========================================================

    PaymentResponseDTO createPayment(
            CreatePaymentRequestDTO request);

    // =========================================================
    // Generate Payment
    // =========================================================

    PaymentResponseDTO generatePayment(
            GeneratePaymentRequestDTO request);

    // =========================================================
    // Scheduled Monthly Payments
    // =========================================================

    void generateMonthlyPayments();

    // =========================================================
    // OWNER - Get All Payments
    // =========================================================

    PageResponseDTO<PaymentResponseDTO> getAllPayments(
            int page,
            int size,
            String sortBy,
            String direction,
            PaymentStatus status);

    // =========================================================
    // OWNER - Get Payment By ID
    // =========================================================

    PaymentResponseDTO getPaymentById(
            Long paymentId);

    // =========================================================
    // OWNER - Update Payment
    // =========================================================

    PaymentResponseDTO updatePayment(
            Long paymentId,
            UpdatePaymentRequestDTO request);

    // =========================================================
    // TENANT - Get My Payments
    // =========================================================

    PageResponseDTO<PaymentResponseDTO> getMyPayments(
            int page,
            int size,
            String sortBy,
            String direction,
            PaymentStatus status);

    // =========================================================
    // TENANT - Get My Payment By ID
    // =========================================================

    PaymentResponseDTO getMyPaymentById(
            Long paymentId);

    // =========================================================
    // OWNER - Mark Payment As Paid
    // =========================================================

    PaymentResponseDTO markPaymentAsPaid(
            Long paymentId);

    // =========================================================
    // OWNER - Get Payments By Status
    // =========================================================

    PageResponseDTO<PaymentResponseDTO> getPaymentsByStatus(
            PaymentStatus status,
            int page,
            int size,
            String sortBy,
            String direction);

    // =========================================================
    // Scheduled Overdue Payments
    // =========================================================

    void updateOverduePayments();

	
}