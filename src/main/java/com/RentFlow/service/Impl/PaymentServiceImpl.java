package com.RentFlow.service.Impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.CreatePaymentRequestDTO;
import com.RentFlow.dto.request.GeneratePaymentRequestDTO;
import com.RentFlow.dto.request.UpdatePaymentRequestDTO;
import com.RentFlow.dto.response.PageResponseDTO;
import com.RentFlow.dto.response.PaymentResponseDTO;
import com.RentFlow.entity.Lease;
import com.RentFlow.entity.Payment;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.LeaseStatus;
import com.RentFlow.enums.NotificationType;
import com.RentFlow.enums.PaymentStatus;
import com.RentFlow.exception.AccessDeniedException;
import com.RentFlow.exception.BadRequestException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.LeaseRepository;
import com.RentFlow.repository.PaymentRepository;
import com.RentFlow.repository.TenantRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.NotificationService;
import com.RentFlow.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            LeaseRepository leaseRepository,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            ModelMapper modelMapper,
            NotificationService notificationService) {

        this.paymentRepository = paymentRepository;
        this.leaseRepository = leaseRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.notificationService = notificationService;
    }

    // =========================================================
    // Get Current User
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }

    // =========================================================
    // Get Tenant User
    // =========================================================

    private User getTenantUser(Tenant tenant) {

        return userRepository.findByEmail(
                tenant.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant user not found"));
    }

    // =========================================================
    // Check Owner
    // =========================================================

    private void checkOwner(
            Payment payment,
            User currentUser) {

        if (!payment.getLease()
                .getProperty()
                .getOwner()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to manage this payment");
        }
    }


    private Pageable createPageable(
            int page,
            int size,
            String sortBy,
            String direction) {

        if (page < 0) {

            throw new BadRequestException(
                    "Page number cannot be negative");
        }

        if (size <= 0) {

            throw new BadRequestException(
                    "Page size must be greater than zero");
        }

        if (size > 100) {

            throw new BadRequestException(
                    "Page size cannot be greater than 100");
        }

        if (sortBy == null ||
                sortBy.trim().isEmpty()) {

            sortBy = "id";
        }

        Sort sort;

        if ("asc".equalsIgnoreCase(direction)) {

            sort = Sort.by(sortBy)
                    .ascending();

        } else {

            sort = Sort.by(sortBy)
                    .descending();
        }

        return PageRequest.of(
                page,
                size,
                sort);
    }
    
    private PageResponseDTO<PaymentResponseDTO>
    convertToPageResponse(
            Page<Payment> paymentPage) {

        PageResponseDTO<PaymentResponseDTO> response =
                new PageResponseDTO<>();

        response.setContent(
                paymentPage.getContent()
                        .stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList())
        );

        response.setPageNumber(
                paymentPage.getNumber());

        response.setPageSize(
                paymentPage.getSize());

        response.setTotalElements(
                paymentPage.getTotalElements());

        response.setTotalPages(
                paymentPage.getTotalPages());

        response.setFirst(
                paymentPage.isFirst());

        response.setLast(
                paymentPage.isLast());

        return response;
    }
    
    // =========================================================
    // Create Payment
    // OWNER
    // =========================================================

    @Override
    public PaymentResponseDTO createPayment(
            CreatePaymentRequestDTO request) {

        User currentUser = getCurrentUser();

        Lease lease =
                leaseRepository.findById(
                        request.getLeaseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lease not found"));

        // Owner authorization
        if (!lease.getProperty()
                .getOwner()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to create payment for this lease");
        }

        // Prevent duplicate payment
        if (paymentRepository.existsByLeaseAndDueDate(
                lease,
                request.getDueDate())) {

            throw new BadRequestException(
                    "Payment already exists for this due date");
        }

        Payment payment = new Payment();

        payment.setLease(lease);
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setDueDate(request.getDueDate());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionReference(
                request.getTransactionReference());

        /*
         * createPayment() represents a payment
         * recorded as already received by owner.
         */
        payment.setStatus(PaymentStatus.PAID);

        Payment savedPayment =
                paymentRepository.save(payment);

        /*
         * Notify tenant that payment has been received.
         */
        Tenant tenant = lease.getTenant();

        User tenantUser = getTenantUser(tenant);

        notificationService.createNotification(
                tenantUser.getId(),
                NotificationType.PAYMENT_RECEIVED,
                "Payment Received",
                "Your payment of ₹"
                        + savedPayment.getAmount()
                        + " for "
                        + lease.getProperty()
                                .getPropertyName()
                        + " has been marked as paid."
        );

        return convertToResponse(savedPayment);
    }

    // =========================================================
    // Get All Payments
    // OWNER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PaymentResponseDTO> getAllPayments(
            int page,
            int size,
            String sortBy,
            String direction,
            PaymentStatus status) {

        User currentUser = getCurrentUser();

        Pageable pageable =
                createPageable(
                        page,
                        size,
                        sortBy,
                        direction);

        Page<Payment> paymentPage;

        if (status != null) {

            paymentPage =
                    paymentRepository
                            .findByLeasePropertyOwnerAndStatus(
                                    currentUser,
                                    status,
                                    pageable);

        } else {

            paymentPage =
                    paymentRepository
                            .findByLeasePropertyOwner(
                                    currentUser,
                                    pageable);
        }

        return convertToPageResponse(paymentPage);
    }

    // =========================================================
    // Get Payment By ID
    // OWNER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(
            Long paymentId) {

        User currentUser = getCurrentUser();

        Payment payment =
                paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"));

        checkOwner(payment, currentUser);

        return convertToResponse(payment);
    }

    // =========================================================
    // Update Payment
    // OWNER
    // =========================================================

    @Override
    public PaymentResponseDTO updatePayment(
            Long paymentId,
            UpdatePaymentRequestDTO request) {

        User currentUser = getCurrentUser();

        Payment payment =
                paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"));

        checkOwner(payment, currentUser);

        /*
         * Only update payment information here.
         *
         * IMPORTANT:
         * PAYMENT_RECEIVED notification is NOT created here.
         * It is created only inside markPaymentAsPaid().
         */

        if (request.getAmount() != null) {

            payment.setAmount(
                    request.getAmount());
        }

        if (request.getPaymentDate() != null) {

            payment.setPaymentDate(
                    request.getPaymentDate());
        }

        if (request.getDueDate() != null) {

            payment.setDueDate(
                    request.getDueDate());
        }

        if (request.getPaymentMethod() != null) {

            payment.setPaymentMethod(
                    request.getPaymentMethod());
        }

        if (request.getTransactionReference()
                != null) {

            payment.setTransactionReference(
                    request.getTransactionReference());
        }

        Payment updatedPayment =
                paymentRepository.save(payment);

        return convertToResponse(updatedPayment);
    }

    // =========================================================
    // Get My Payments
    // TENANT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PaymentResponseDTO> getMyPayments(
            int page,
            int size,
            String sortBy,
            String direction,
            PaymentStatus status) {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository.findByEmail(
                        currentUser.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant profile not found"));

        Pageable pageable =
                createPageable(
                        page,
                        size,
                        sortBy,
                        direction);

        Page<Payment> paymentPage;

        if (status != null) {

            paymentPage =
                    paymentRepository
                            .findByLeaseTenantAndStatus(
                                    tenant,
                                    status,
                                    pageable);

        } else {

            paymentPage =
                    paymentRepository
                            .findByLeaseTenant(
                                    tenant,
                                    pageable);
        }

        return convertToPageResponse(paymentPage);
    }

    // =========================================================
    // Get My Payment By ID
    // TENANT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getMyPaymentById(
            Long paymentId) {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository.findByEmail(
                        currentUser.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant profile not found"));

        Payment payment =
                paymentRepository
                        .findByIdAndLeaseTenant(
                                paymentId,
                                tenant)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"));

        return convertToResponse(payment);
    }

    // =========================================================
    // Convert Entity → DTO
    // =========================================================

    private PaymentResponseDTO convertToResponse(
            Payment payment) {

        PaymentResponseDTO response =
                modelMapper.map(
                        payment,
                        PaymentResponseDTO.class);

        Lease lease = payment.getLease();

        response.setLeaseId(
                lease.getId());

        Tenant tenant =
                lease.getTenant();

        response.setTenantId(
                tenant.getId());

        response.setTenantName(
                tenant.getFirstName()
                + " "
                + tenant.getLastName());

        Property property =
                lease.getProperty();

        response.setPropertyId(
                property.getId());

        response.setPropertyName(
                property.getPropertyName());

        return response;
    }

    // =========================================================
    // Mark Payment As Paid
    // OWNER
    // =========================================================

    @Override
    public PaymentResponseDTO markPaymentAsPaid(
            Long paymentId) {

        User currentUser = getCurrentUser();

        Payment payment =
                paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"));

        checkOwner(payment, currentUser);

        if (payment.getStatus() ==
                PaymentStatus.PAID) {

            throw new BadRequestException(
                    "Payment is already marked as PAID");
        }

        /*
         * Payment must be PENDING or OVERDUE.
         */
        if (payment.getStatus() !=
                    PaymentStatus.PENDING
                &&
                payment.getStatus() !=
                    PaymentStatus.OVERDUE) {

            throw new BadRequestException(
                    "Only PENDING or OVERDUE payments can be marked as PAID");
        }

        payment.setStatus(
                PaymentStatus.PAID);

        payment.setPaymentDate(
                LocalDate.now());

        Payment updatedPayment =
                paymentRepository.save(payment);

        // =====================================================
        // PAYMENT_RECEIVED notification
        // =====================================================

        User tenantUser =
                userRepository.findByEmail(
                        payment.getLease()
                                .getTenant()
                                .getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant user not found"));

        notificationService.createNotification(
                tenantUser.getId(),
                NotificationType.PAYMENT_RECEIVED,
                "Payment Received",
                "Your payment of ₹"
                        + payment.getAmount()
                        + " for property "
                        + payment.getLease()
                                .getProperty()
                                .getPropertyName()
                        + " has been marked as paid."
        );

        return convertToResponse(updatedPayment);
    }

    // =========================================================
    // Update Overdue Payments
    // Runs every midnight
    // =========================================================

    @Scheduled(cron = "0 0 0 * * *")
    @Override
    public void updateOverduePayments() {

        LocalDate today =
                LocalDate.now();

        List<Payment> payments =
                paymentRepository
                        .findByStatusAndDueDateBefore(
                                PaymentStatus.PENDING,
                                today);

        if (payments.isEmpty()) {
            return;
        }

        for (Payment payment : payments) {

            payment.setStatus(
                    PaymentStatus.OVERDUE);

            // =================================================
            // PAYMENT_OVERDUE notification
            // =================================================

            Tenant tenant =
                    payment.getLease().getTenant();

            User tenantUser =
                    userRepository.findByEmail(
                            tenant.getEmail())
                    .orElse(null);

            if (tenantUser != null) {

                notificationService.createNotification(
                        tenantUser.getId(),
                        NotificationType.PAYMENT_OVERDUE,
                        "Payment Overdue",
                        "Your rent payment of ₹"
                                + payment.getAmount()
                                + " was due on "
                                + payment.getDueDate()
                                + " and is now overdue."
                );
            }
        }

        paymentRepository.saveAll(payments);
    }

    // =========================================================
    // Generate Payment
    // OWNER
    // =========================================================

    @Override
    public PaymentResponseDTO generatePayment(
            GeneratePaymentRequestDTO request) {

        User currentUser = getCurrentUser();

        Lease lease =
                leaseRepository.findById(
                        request.getLeaseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lease not found"));

        // Owner authorization
        if (!lease.getProperty()
                .getOwner()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to generate payment for this lease");
        }

        // Only ACTIVE lease
        if (lease.getStatus() !=
                LeaseStatus.ACTIVE) {

            throw new BadRequestException(
                    "Payment cannot be generated for an inactive lease");
        }

        // Duplicate check
        if (paymentRepository.existsByLeaseAndDueDate(
                lease,
                request.getDueDate())) {

            throw new BadRequestException(
                    "Payment already exists for this due date");
        }

        Payment payment = new Payment();

        payment.setLease(lease);

        /*
         * Rent comes from lease.
         */
        payment.setAmount(
                lease.getMonthlyRent());

        payment.setDueDate(
                request.getDueDate());

        payment.setPaymentDate(null);

        payment.setStatus(
                PaymentStatus.PENDING);

        payment.setPaymentMethod(null);

        payment.setTransactionReference(null);

        Payment savedPayment =
                paymentRepository.save(payment);

        // =====================================================
        // PAYMENT_DUE notification
        // =====================================================

        Tenant tenant =
                lease.getTenant();

        User tenantUser =
                getTenantUser(tenant);

        notificationService.createNotification(
                tenantUser.getId(),
                NotificationType.PAYMENT_DUE,
                "Payment Due",
                "Your rent payment of ₹"
                        + lease.getMonthlyRent()
                        + " is due on "
                        + request.getDueDate()
                        + " for property "
                        + lease.getProperty()
                                .getPropertyName()
        );

        return convertToResponse(savedPayment);
    }

    // =========================================================
    // Generate Monthly Payments
    // Runs every day at 1 AM
    // =========================================================

    @Scheduled(cron = "0 0 1 * * *")
    @Override
    public void generateMonthlyPayments() {

        LocalDate today =
                LocalDate.now();

        List<Lease> activeLeases =
                leaseRepository.findByStatus(
                        LeaseStatus.ACTIVE);

        for (Lease lease : activeLeases) {

            LocalDate leaseStartDate =
                    lease.getStartDate();

            /*
             * Do not generate before lease starts.
             */
            if (today.isBefore(leaseStartDate)) {
                continue;
            }

            /*
             * Calculate billing date.
             */
            LocalDate billingDate =
                    leaseStartDate
                            .withYear(today.getYear())
                            .withMonth(today.getMonthValue());

            /*
             * Handle dates such as 29/30/31.
             */
            int lastDay =
                    billingDate.lengthOfMonth();

            int day =
                    Math.min(
                            leaseStartDate.getDayOfMonth(),
                            lastDay);

            billingDate =
                    billingDate.withDayOfMonth(day);

            /*
             * Don't generate future payment.
             */
            if (billingDate.isAfter(today)) {
                continue;
            }

            /*
             * Prevent duplicate payment.
             */
            if (paymentRepository.existsByLeaseAndDueDate(
                    lease,
                    billingDate)) {

                continue;
            }

            Payment payment =
                    new Payment();

            payment.setLease(lease);

            payment.setAmount(
                    lease.getMonthlyRent());

            payment.setDueDate(
                    billingDate);

            payment.setPaymentDate(null);

            payment.setStatus(
                    PaymentStatus.PENDING);

            payment.setPaymentMethod(null);

            payment.setTransactionReference(null);

            Payment savedPayment =
                    paymentRepository.save(payment);

            // =================================================
            // PAYMENT_DUE notification
            // =================================================

            Tenant tenant =
                    lease.getTenant();

            User tenantUser =
                    userRepository.findByEmail(
                            tenant.getEmail())
                    .orElse(null);

            if (tenantUser != null) {

                notificationService.createNotification(
                        tenantUser.getId(),
                        NotificationType.PAYMENT_DUE,
                        "Monthly Rent Payment Due",
                        "Your monthly rent of ₹"
                                + savedPayment.getAmount()
                                + " is due on "
                                + billingDate
                                + " for property "
                                + lease.getProperty()
                                        .getPropertyName()
                );
            }
        }
    }

    // =========================================================
    // Get Payments By Status
    // OWNER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PaymentResponseDTO> getPaymentsByStatus(
            PaymentStatus status,
            int page,
            int size,
            String sortBy,
            String direction) {

        User currentUser = getCurrentUser();

        Pageable pageable =
                createPageable(
                        page,
                        size,
                        sortBy,
                        direction);

        Page<Payment> paymentPage =
                paymentRepository
                        .findByLeasePropertyOwnerAndStatus(
                                currentUser,
                                status,
                                pageable);

        return convertToPageResponse(
                paymentPage);
    }



}