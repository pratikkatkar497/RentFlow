package com.RentFlow.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.request.CreatePaymentRequestDTO;
import com.RentFlow.dto.request.GeneratePaymentRequestDTO;
import com.RentFlow.dto.request.UpdatePaymentRequestDTO;
import com.RentFlow.dto.response.PageResponseDTO;
import com.RentFlow.dto.response.PaymentResponseDTO;
import com.RentFlow.enums.PaymentStatus;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // =========================================================
    // Create Payment
    // OWNER / MANAGER
    // =========================================================

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> createPayment(
            @Valid @RequestBody CreatePaymentRequestDTO request) {

        PaymentResponseDTO response =
                paymentService.createPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        AppConstants.CREATED,
                        response));
    }

    // =========================================================
    // Generate Payment
    // OWNER / MANAGER
    // Creates PENDING payment
    // =========================================================

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> generatePayment(
            @Valid @RequestBody GeneratePaymentRequestDTO request) {

        PaymentResponseDTO response =
                paymentService.generatePayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Payment generated successfully",
                        response));
    }

    // =========================================================
    // Get All Payments
    // OWNER / MANAGER
    //
    // Example:
    // GET /api/payments?page=0&size=10
    //
    // With sorting:
    // GET /api/payments?page=0&size=10&sortBy=dueDate&direction=desc
    //
    // With status:
    // GET /api/payments?page=0&size=10&status=PENDING
    // =========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<PageResponseDTO<PaymentResponseDTO>>>
            getAllPayments(

                    @RequestParam(
                            defaultValue = "0")
                    int page,

                    @RequestParam(
                            defaultValue = "10")
                    int size,

                    @RequestParam(
                            defaultValue = "id")
                    String sortBy,

                    @RequestParam(
                            defaultValue = "desc")
                    String direction,

                    @RequestParam(
                            required = false)
                    PaymentStatus status) {

        PageResponseDTO<PaymentResponseDTO> response =
                paymentService.getAllPayments(
                        page,
                        size,
                        sortBy,
                        direction,
                        status);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // =========================================================
    // Get Payment By ID
    // OWNER / MANAGER
    // =========================================================

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>>
            getPaymentById(
                    @PathVariable Long paymentId) {

        PaymentResponseDTO response =
                paymentService.getPaymentById(
                        paymentId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // =========================================================
    // Update Payment
    // OWNER / MANAGER
    // =========================================================

    @PutMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>>
            updatePayment(

                    @PathVariable Long paymentId,

                    @Valid
                    @RequestBody
                    UpdatePaymentRequestDTO request) {

        PaymentResponseDTO response =
                paymentService.updatePayment(
                        paymentId,
                        request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.UPDATED,
                        response));
    }

    // =========================================================
    // Mark Payment As Paid
    // OWNER / MANAGER
    // =========================================================

    @PatchMapping("/{paymentId}/pay")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>>
            markPaymentAsPaid(
                    @PathVariable Long paymentId) {

        PaymentResponseDTO response =
                paymentService.markPaymentAsPaid(
                        paymentId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payment marked as paid successfully",
                        response));
    }

    // =========================================================
    // Get Payments By Status
    // OWNER / MANAGER
    //
    // Example:
    // GET /api/payments/status/PENDING?page=0&size=10
    //
    // GET /api/payments/status/PAID?page=0&size=5
    // =========================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<
            ApiResponse<PageResponseDTO<PaymentResponseDTO>>>
            getPaymentsByStatus(

                    @PathVariable
                    PaymentStatus status,

                    @RequestParam(
                            defaultValue = "0")
                    int page,

                    @RequestParam(
                            defaultValue = "10")
                    int size,

                    @RequestParam(
                            defaultValue = "dueDate")
                    String sortBy,

                    @RequestParam(
                            defaultValue = "desc")
                    String direction) {

        PageResponseDTO<PaymentResponseDTO> response =
                paymentService.getPaymentsByStatus(
                        status,
                        page,
                        size,
                        sortBy,
                        direction);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // =========================================================
    // Get My Payments
    // TENANT
    //
    // Example:
    // GET /api/payments/my?page=0&size=10
    //
    // GET /api/payments/my?page=0&size=10&status=PENDING
    // =========================================================

    @GetMapping("/my")
    public ResponseEntity<
            ApiResponse<PageResponseDTO<PaymentResponseDTO>>>
            getMyPayments(

                    @RequestParam(
                            defaultValue = "0")
                    int page,

                    @RequestParam(
                            defaultValue = "10")
                    int size,

                    @RequestParam(
                            defaultValue = "dueDate")
                    String sortBy,

                    @RequestParam(
                            defaultValue = "desc")
                    String direction,

                    @RequestParam(
                            required = false)
                    PaymentStatus status) {

        PageResponseDTO<PaymentResponseDTO> response =
                paymentService.getMyPayments(
                        page,
                        size,
                        sortBy,
                        direction,
                        status);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // =========================================================
    // Get My Payment By ID
    // TENANT
    // =========================================================

    @GetMapping("/my/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>>
            getMyPaymentById(
                    @PathVariable Long paymentId) {

        PaymentResponseDTO response =
                paymentService.getMyPaymentById(
                        paymentId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }
}