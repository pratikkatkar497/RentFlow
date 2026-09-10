
package com.RentFlow.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.response.AdminDashboardResponseDTO;
import com.RentFlow.enums.LeaseStatus;
import com.RentFlow.enums.PaymentStatus;
import com.RentFlow.enums.RoleType;
import com.RentFlow.repository.LeaseRepository;
import com.RentFlow.repository.OrganizationRepository;
import com.RentFlow.repository.PaymentRepository;
import com.RentFlow.repository.PropertyRepository;
import com.RentFlow.repository.TenantRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.AdminDashboardService;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardServiceImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            LeaseRepository leaseRepository,
            PaymentRepository paymentRepository) {

        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.leaseRepository = leaseRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public AdminDashboardResponseDTO getAdminDashboard() {

        AdminDashboardResponseDTO response =
                new AdminDashboardResponseDTO();

        // =====================================================
        // ORGANIZATIONS
        // =====================================================

        long totalOrganizations =
                organizationRepository.count();

        long activeOrganizations =
                organizationRepository.countByActiveTrue();

        // =====================================================
        // USERS
        // =====================================================

        long totalUsers =
                userRepository.count();

        long totalOwners =
                userRepository.countByRole_Name(
                        RoleType.OWNER);

        long totalManagers =
                userRepository.countByRole_Name(
                        RoleType.MANAGER);

        long totalTenants =
                userRepository.countByRole_Name(
                        RoleType.TENANT);

        // =====================================================
        // PROPERTIES
        // =====================================================

        long totalProperties =
                propertyRepository.count();

        // =====================================================
        // LEASES
        // =====================================================

        long totalLeases =
                leaseRepository.count();

        long activeLeases =
                leaseRepository.countByStatus(
                        LeaseStatus.ACTIVE);

        long terminatedLeases =
                leaseRepository.countByStatus(
                        LeaseStatus.TERMINATED);

        long expiredLeases =
                leaseRepository.countByStatus(
                        LeaseStatus.EXPIRED);

        // =====================================================
        // PAYMENTS
        // =====================================================

        long totalPayments =
                paymentRepository.count();

        long paidPayments =
                paymentRepository.countByStatus(
                        PaymentStatus.PAID);

        long pendingPayments =
                paymentRepository.countByStatus(
                        PaymentStatus.PENDING);

        long overduePayments =
                paymentRepository.countByStatus(
                        PaymentStatus.OVERDUE);

        // =====================================================
        // BUILD RESPONSE
        // =====================================================

        response.setTotalOrganizations(
                totalOrganizations);

        response.setActiveOrganizations(
                activeOrganizations);

        response.setTotalUsers(
                totalUsers);

        response.setTotalOwners(
                totalOwners);

        response.setTotalManagers(
                totalManagers);

        response.setTotalTenants(
                totalTenants);

        response.setTotalProperties(
                totalProperties);

        response.setTotalLeases(
                totalLeases);

        response.setActiveLeases(
                activeLeases);

        response.setTerminatedLeases(
                terminatedLeases);

        response.setExpiredLeases(
                expiredLeases);

        response.setTotalPayments(
                totalPayments);

        response.setPaidPayments(
                paidPayments);

        response.setPendingPayments(
                pendingPayments);

        response.setOverduePayments(
                overduePayments);

        return response;
    }
}

