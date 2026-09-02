package com.RentFlow.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.RentFlow.entity.Lease;
import com.RentFlow.entity.Payment;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.PaymentStatus;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByLease(Lease lease);

    List<Payment> findByLeaseAndStatus(
            Lease lease,
            PaymentStatus status);

    Optional<Payment> findByIdAndLease(
            Long id,
            Lease lease);

    boolean existsByLeaseAndDueDate(
            Lease lease,
            LocalDate dueDate);

    List<Payment> findByStatusAndDueDateBefore(
            PaymentStatus status,
            LocalDate date);

    List<Payment> findByLeasePropertyOwnerAndStatus(
            User owner,
            PaymentStatus status);

    List<Payment> findByLeasePropertyOwner(
            User owner);

    List<Payment> findByLeaseTenantOrderByDueDateAsc(
            Tenant tenant);

    List<Payment> findByLeaseTenantOrderByDueDateDesc(
            Tenant tenant);

    Optional<Payment> findByIdAndLeaseTenant(
            Long paymentId,
            Tenant tenant);

    Optional<Payment>
    findFirstByLeaseAndStatusInAndDueDateGreaterThanEqualOrderByDueDateAsc(
            Lease lease,
            List<PaymentStatus> statuses,
            LocalDate dueDate);


    // =========================================================
    // OWNER PAGINATION
    // =========================================================

    Page<Payment> findByLeasePropertyOwner(
            User owner,
            Pageable pageable);


    // =========================================================
    // TENANT PAGINATION
    // =========================================================

    Page<Payment> findByLeaseTenant(
            Tenant tenant,
            Pageable pageable);

    Page<Payment> findByLeaseTenantAndStatus(
            Tenant tenant,
            PaymentStatus status,
            Pageable pageable);

	Page<Payment> findByLeasePropertyOwnerAndStatus(User currentUser, PaymentStatus status, Pageable pageable);

	List<Payment> findByLeaseOrderByDueDateAsc(Lease lease);
    
    
}