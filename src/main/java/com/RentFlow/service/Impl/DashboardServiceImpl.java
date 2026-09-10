package com.RentFlow.service.Impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.RentFlow.dto.response.DashboardResponseDTO;
import com.RentFlow.dto.response.TenantDashboardResponseDTO;
import com.RentFlow.entity.Lease;
import com.RentFlow.entity.Payment;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.LeaseStatus;
import com.RentFlow.enums.PaymentStatus;
import com.RentFlow.exception.AccessDeniedException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.LeaseRepository;
import com.RentFlow.repository.PaymentRepository;
import com.RentFlow.repository.PropertyRepository;
import com.RentFlow.repository.TenantRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public DashboardServiceImpl(
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            LeaseRepository leaseRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository) {

        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.leaseRepository = leaseRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // Get Current Logged-in User
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
    // OWNER DASHBOARD
    // =========================================================

    @Override
    public DashboardResponseDTO getOwnerDashboard() {

        User currentUser = getCurrentUser();

        // -----------------------------------------------------
        // Get owner's properties
        // -----------------------------------------------------

        List<Property> properties =
                propertyRepository
                        .findByOwner(currentUser);

        long totalProperties =
                properties.size();

        // -----------------------------------------------------
        // Available / Occupied Properties
        // -----------------------------------------------------

        long availableProperties = 0;
        long occupiedProperties = 0;

        for (Property property : properties) {

            boolean occupied =
                    leaseRepository
                            .existsByPropertyAndStatus(
                                    property,
                                    LeaseStatus.ACTIVE);

            if (occupied) {
                occupiedProperties++;
            } else {
                availableProperties++;
            }
        }

        // -----------------------------------------------------
        // Occupancy Rate
        // -----------------------------------------------------

        double occupancyRate = 0.0;

        if (totalProperties > 0) {

            occupancyRate =
                    ((double) occupiedProperties
                            / totalProperties) * 100;
        }

        // -----------------------------------------------------
        // Active Leases
        // -----------------------------------------------------

        List<Lease> activeLeases =
                leaseRepository
                        .findByPropertyOwnerAndStatus(
                                currentUser,
                                LeaseStatus.ACTIVE);

        long activeLeasesCount =
                activeLeases.size();

        // -----------------------------------------------------
        // Total Tenants
        // -----------------------------------------------------

        long totalTenants = 0;

        for (Lease lease : activeLeases) {

            if (lease.getTenant() != null) {
                totalTenants++;
            }
        }

        // -----------------------------------------------------
        // Active Lease Rent
        // -----------------------------------------------------

        BigDecimal activeLeaseRent =
                BigDecimal.ZERO;

        for (Lease lease : activeLeases) {

            if (lease.getMonthlyRent() != null) {

                activeLeaseRent =
                        activeLeaseRent.add(
                                lease.getMonthlyRent());
            }
        }

        // -----------------------------------------------------
        // Total Monthly Rent
        // -----------------------------------------------------

        BigDecimal totalMonthlyRent =
                activeLeaseRent;

        // -----------------------------------------------------
        // Payment Amounts
        // -----------------------------------------------------

        List<Payment> payments =
                paymentRepository
                        .findByLeasePropertyOwner(
                                currentUser);

        BigDecimal paidAmount =
                BigDecimal.ZERO;

        BigDecimal pendingAmount =
                BigDecimal.ZERO;

        BigDecimal overdueAmount =
                BigDecimal.ZERO;

        for (Payment payment : payments) {

            if (payment.getAmount() == null) {
                continue;
            }

            if (payment.getStatus()
                    == PaymentStatus.PAID) {

                paidAmount =
                        paidAmount.add(
                                payment.getAmount());
            }

            else if (payment.getStatus()
                    == PaymentStatus.PENDING) {

                pendingAmount =
                        pendingAmount.add(
                                payment.getAmount());
            }

            else if (payment.getStatus()
                    == PaymentStatus.OVERDUE) {

                overdueAmount =
                        overdueAmount.add(
                                payment.getAmount());
            }
        }

        // -----------------------------------------------------
        // Build Response
        // -----------------------------------------------------

        DashboardResponseDTO response =
                new DashboardResponseDTO();

        response.setTotalProperties(
                totalProperties);

        response.setAvailableProperties(
                availableProperties);

        response.setOccupiedProperties(
                occupiedProperties);

        response.setTotalTenants(
                totalTenants);

        response.setActiveLeases(
                activeLeasesCount);

        response.setOccupancyRate(
                occupancyRate);

        response.setActiveLeaseRent(
                activeLeaseRent);

        response.setTotalMonthlyRent(
                totalMonthlyRent);

        response.setPaidAmount(
                paidAmount);

        response.setPendingAmount(
                pendingAmount);

        response.setOverdueAmount(
                overdueAmount);

        return response;
    }

    // =========================================================
    // TENANT DASHBOARD
    // =========================================================

    @Override
    public TenantDashboardResponseDTO getTenantDashboard() {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository
                        .findByEmail(currentUser.getEmail())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant profile not found"));

        TenantDashboardResponseDTO response =
                new TenantDashboardResponseDTO();

        // Tenant information
        response.setTenantId(tenant.getId());

        String firstName = tenant.getFirstName();
        String lastName = tenant.getLastName();

        String tenantName =
                ((firstName == null ? "" : firstName) + " "
                        + (lastName == null ? "" : lastName))
                        .trim();

        response.setTenantName(
                tenantName.isEmpty() ? "Tenant" : tenantName
        );

        /*
         * A tenant may temporarily have no active lease.
         * The dashboard should still load successfully.
         */
        Optional<Lease> activeLease =
                leaseRepository.findByTenantAndStatus(
                        tenant,
                        LeaseStatus.ACTIVE
                );

        if (activeLease.isEmpty()) {
            return response;
        }

        Lease lease = activeLease.get();

        Property property = lease.getProperty();

        // Property information
        if (property != null) {
            response.setPropertyId(property.getId());
            response.setPropertyName(property.getPropertyName());
        }

        // Lease information
        response.setLeaseId(lease.getId());
        response.setLeaseStartDate(lease.getStartDate());
        response.setLeaseEndDate(lease.getEndDate());
        response.setMonthlyRent(lease.getMonthlyRent());

        /*
         * Find the next unpaid payment.
         */
        List<Payment> payments =
                paymentRepository
                        .findByLeaseOrderByDueDateAsc(lease);

        Payment nextPayment = null;

        for (Payment payment : payments) {

            if (payment.getDueDate() == null) {
                continue;
            }

            if (payment.getStatus() == PaymentStatus.PAID) {
                continue;
            }

            nextPayment = payment;
            break;
        }

        if (nextPayment != null) {

            response.setNextDueDate(
                    nextPayment.getDueDate()
            );

            response.setNextPaymentAmount(
                    nextPayment.getAmount()
            );

            response.setNextPaymentStatus(
                    nextPayment.getStatus().name()
            );
        }

        return response;
    }
}