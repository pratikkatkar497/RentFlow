package com.RentFlow.service.Impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.CreateMaintenanceRequestDTO;
import com.RentFlow.dto.request.UpdateMaintenanceRequestDTO;
import com.RentFlow.dto.response.MaintenanceResponseDTO;
import com.RentFlow.entity.Lease;
import com.RentFlow.entity.MaintenanceRequest;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.LeaseStatus;
import com.RentFlow.enums.MaintenanceStatus;
import com.RentFlow.enums.NotificationType;
import com.RentFlow.enums.TenantStatus;
import com.RentFlow.exception.AccessDeniedException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.LeaseRepository;
import com.RentFlow.repository.MaintenanceRequestRepository;
import com.RentFlow.repository.PropertyRepository;
import com.RentFlow.repository.TenantRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.MaintenanceService;
import com.RentFlow.service.NotificationService;

@Service
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRequestRepository maintenanceRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public MaintenanceServiceImpl(
            MaintenanceRequestRepository maintenanceRepository,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            LeaseRepository leaseRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.maintenanceRepository = maintenanceRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.leaseRepository = leaseRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
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
                    "User is not authenticated.");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."));
    }

    // =========================================================
    // Get Current Tenant
    // =========================================================

    private Tenant getCurrentTenant() {

        User currentUser = getCurrentUser();

        return tenantRepository
                .findByEmail(currentUser.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant profile not found."));
    }

    // =========================================================
    // Convert Entity → Response DTO
    // =========================================================

    private MaintenanceResponseDTO convertToResponse(
            MaintenanceRequest maintenance) {

        MaintenanceResponseDTO response =
                new MaintenanceResponseDTO();

        response.setId(
                maintenance.getId());

        Property property =
                maintenance.getProperty();

        Tenant tenant =
                maintenance.getTenant();

        response.setPropertyId(
                property.getId());

        response.setPropertyName(
                property.getPropertyName());

        response.setTenantId(
                tenant.getId());

        response.setTenantName(
                tenant.getFirstName()
                        + " "
                        + tenant.getLastName());

        response.setTitle(
                maintenance.getTitle());

        response.setDescription(
                maintenance.getDescription());

        response.setCategory(
                maintenance.getCategory());

        response.setPriority(
                maintenance.getPriority());

        response.setStatus(
                maintenance.getStatus());

        response.setReportedDate(
                maintenance.getReportedDate());

        response.setResolvedDate(
                maintenance.getResolvedDate());

        response.setAssignedTo(
                maintenance.getAssignedTo());

        response.setEstimatedCost(
                maintenance.getEstimatedCost());

        response.setActualCost(
                maintenance.getActualCost());

        response.setNotes(
                maintenance.getNotes());

        return response;
    }

    // =========================================================
    // Create Maintenance Request
    // TENANT
    // =========================================================

    @Override
    public MaintenanceResponseDTO createMaintenance(
            CreateMaintenanceRequestDTO request) {

        // -----------------------------------------------------
        // Get logged-in tenant
        // -----------------------------------------------------

        Tenant tenant = getCurrentTenant();

        // -----------------------------------------------------
        // Tenant must be active
        // -----------------------------------------------------

        if (tenant.getStatus() != TenantStatus.ACTIVE) {

            throw new AccessDeniedException(
                    "Inactive tenant cannot create maintenance requests.");
        }

        // -----------------------------------------------------
        // Find property
        // -----------------------------------------------------

        Property property =
                propertyRepository.findById(
                        request.getPropertyId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found."));

        // -----------------------------------------------------
        // Verify active lease
        // -----------------------------------------------------

        leaseRepository
                .findByTenantAndPropertyAndStatus(
                        tenant,
                        property,
                        LeaseStatus.ACTIVE)
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Tenant does not have an active lease for this property."));

        // -----------------------------------------------------
        // Create maintenance request
        // -----------------------------------------------------

        MaintenanceRequest maintenance =
                new MaintenanceRequest();

        maintenance.setProperty(property);

        maintenance.setTenant(tenant);

        maintenance.setTitle(
                request.getTitle());

        maintenance.setDescription(
                request.getDescription());

        maintenance.setCategory(
                request.getCategory());

        maintenance.setPriority(
                request.getPriority());

        maintenance.setStatus(
                MaintenanceStatus.OPEN);

        maintenance.setReportedDate(
                LocalDate.now());

        MaintenanceRequest saved =
                maintenanceRepository.save(
                        maintenance);

        // -----------------------------------------------------
        // Notify Property Owner
        // -----------------------------------------------------

        User owner =
                property.getOwner();

        if (owner != null) {

            notificationService.createNotification(
                    owner.getId(),
                    NotificationType.MAINTENANCE_CREATED,
                    "New Maintenance Request",
                    "Tenant "
                            + tenant.getFirstName()
                            + " "
                            + tenant.getLastName()
                            + " has reported a maintenance issue: "
                            + saved.getTitle()
                            + " for property "
                            + property.getPropertyName()
            );
        }

        return convertToResponse(saved);
    }

    // =========================================================
    // Get All Maintenance Requests
    // OWNER / TENANT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponseDTO> getAllMaintenance() {

        User currentUser = getCurrentUser();

        List<MaintenanceRequest> requests =
                maintenanceRepository.findAll();

        return requests.stream()
                .filter(request -> {

                    boolean isOwner =
                            request.getProperty()
                                    .getOwner()
                                    .getId()
                                    .equals(currentUser.getId());

                    boolean isTenant =
                            request.getTenant()
                                    .getEmail()
                                    .equals(currentUser.getEmail());

                    return isOwner || isTenant;
                })
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // Get Maintenance By ID
    // OWNER / TENANT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public MaintenanceResponseDTO getMaintenanceById(
            Long id) {

        User currentUser = getCurrentUser();

        MaintenanceRequest maintenance =
                maintenanceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found."));

        boolean isOwner =
                maintenance.getProperty()
                        .getOwner()
                        .getId()
                        .equals(currentUser.getId());

        boolean isTenant =
                maintenance.getTenant()
                        .getEmail()
                        .equals(currentUser.getEmail());

        if (!isOwner && !isTenant) {

            throw new AccessDeniedException(
                    "You are not authorized to access this maintenance request.");
        }

        return convertToResponse(maintenance);
    }

    // =========================================================
    // Get Maintenance By Property
    // OWNER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponseDTO> getMaintenanceByProperty(
            Long propertyId) {

        User currentUser = getCurrentUser();

        Property property =
                propertyRepository.findById(propertyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found."));

        // -----------------------------------------------------
        // Only property owner can access
        // -----------------------------------------------------

        if (property.getOwner() == null ||
                !property.getOwner()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to access maintenance requests for this property.");
        }

        return maintenanceRepository
                .findByProperty(property)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // Get Maintenance By Tenant
    // OWNER / TENANT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponseDTO> getMaintenanceByTenant(
            Long tenantId) {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository.findById(tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found."));

        // -----------------------------------------------------
        // Tenant can access own requests
        // -----------------------------------------------------

        boolean isOwnTenant =
                tenant.getEmail()
                        .equals(currentUser.getEmail());

        // -----------------------------------------------------
        // Owner can access tenant's requests
        // -----------------------------------------------------

        boolean isOwner = false;

        if (tenant.getProperty() != null &&
                tenant.getProperty().getOwner() != null) {

            isOwner =
                    tenant.getProperty()
                            .getOwner()
                            .getId()
                            .equals(currentUser.getId());
        }

        if (!isOwnTenant && !isOwner) {

            throw new AccessDeniedException(
                    "You are not authorized to access this tenant's maintenance requests.");
        }

        return maintenanceRepository
                .findByTenant(tenant)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // Update Maintenance
    // OWNER
    // =========================================================

    @Override
    public MaintenanceResponseDTO updateMaintenance(
            Long id,
            UpdateMaintenanceRequestDTO request) {

        User currentUser = getCurrentUser();

        MaintenanceRequest maintenance =
                maintenanceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found."));

        // -----------------------------------------------------
        // Only property owner can update
        // -----------------------------------------------------

        checkOwner(
                maintenance,
                currentUser);

        // -----------------------------------------------------
        // CLOSED request cannot be modified
        // -----------------------------------------------------

        if (maintenance.getStatus() ==
                MaintenanceStatus.CLOSED) {

            throw new IllegalArgumentException(
                    "Closed maintenance request cannot be modified.");
        }

        // -----------------------------------------------------
        // Update fields
        // -----------------------------------------------------

        if (request.getTitle() != null) {

            maintenance.setTitle(
                    request.getTitle());
        }

        if (request.getDescription() != null) {

            maintenance.setDescription(
                    request.getDescription());
        }

        if (request.getCategory() != null) {

            maintenance.setCategory(
                    request.getCategory());
        }

        if (request.getPriority() != null) {

            maintenance.setPriority(
                    request.getPriority());
        }

        if (request.getAssignedTo() != null) {

            maintenance.setAssignedTo(
                    request.getAssignedTo());
        }

        if (request.getEstimatedCost() != null) {

            maintenance.setEstimatedCost(
                    request.getEstimatedCost());
        }

        if (request.getActualCost() != null) {

            maintenance.setActualCost(
                    request.getActualCost());
        }

        if (request.getNotes() != null) {

            maintenance.setNotes(
                    request.getNotes());
        }

        MaintenanceRequest updated =
                maintenanceRepository.save(
                        maintenance);

        return convertToResponse(updated);
    }

    // =========================================================
    // OPEN → IN_PROGRESS
    // OWNER
    // =========================================================

    @Override
    public void startMaintenance(Long id) {

        User currentUser = getCurrentUser();

        MaintenanceRequest maintenance =
                maintenanceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found."));

        // -----------------------------------------------------
        // Owner authorization
        // -----------------------------------------------------

        checkOwner(
                maintenance,
                currentUser);

        // -----------------------------------------------------
        // Validate current status
        // -----------------------------------------------------

        if (maintenance.getStatus() !=
                MaintenanceStatus.OPEN) {

            throw new IllegalArgumentException(
                    "Only OPEN maintenance requests can be started.");
        }

        // -----------------------------------------------------
        // Change status
        // -----------------------------------------------------

        maintenance.setStatus(
                MaintenanceStatus.IN_PROGRESS);

        MaintenanceRequest updated =
                maintenanceRepository.save(
                        maintenance);

        // -----------------------------------------------------
        // Notify Tenant
        // -----------------------------------------------------

        notifyTenant(
                updated,
                NotificationType.MAINTENANCE_STARTED,
                "Maintenance Request Started",
                "Your maintenance request '"
                        + updated.getTitle()
                        + "' for property "
                        + updated.getProperty()
                                .getPropertyName()
                        + " has been started."
        );
    }

    // =========================================================
    // IN_PROGRESS → RESOLVED
    // OWNER
    // =========================================================

    @Override
    public void resolveMaintenance(Long id) {

        User currentUser = getCurrentUser();

        MaintenanceRequest maintenance =
                maintenanceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found."));

        // -----------------------------------------------------
        // Owner authorization
        // -----------------------------------------------------

        checkOwner(
                maintenance,
                currentUser);

        // -----------------------------------------------------
        // Validate status
        // -----------------------------------------------------

        if (maintenance.getStatus() !=
                MaintenanceStatus.IN_PROGRESS) {

            throw new IllegalArgumentException(
                    "Only IN_PROGRESS maintenance requests can be resolved.");
        }

        // -----------------------------------------------------
        // Update status
        // -----------------------------------------------------

        maintenance.setStatus(
                MaintenanceStatus.RESOLVED);

        maintenance.setResolvedDate(
                LocalDate.now());

        MaintenanceRequest updated =
                maintenanceRepository.save(
                        maintenance);

        // -----------------------------------------------------
        // Notify Tenant
        // -----------------------------------------------------

        notifyTenant(
                updated,
                NotificationType.MAINTENANCE_RESOLVED,
                "Maintenance Request Resolved",
                "Your maintenance request '"
                        + updated.getTitle()
                        + "' for property "
                        + updated.getProperty()
                                .getPropertyName()
                        + " has been resolved."
        );
    }

    // =========================================================
    // RESOLVED → CLOSED
    // OWNER
    // =========================================================

    @Override
    public void closeMaintenance(Long id) {

        User currentUser = getCurrentUser();

        MaintenanceRequest maintenance =
                maintenanceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found."));

        // -----------------------------------------------------
        // Owner authorization
        // -----------------------------------------------------

        checkOwner(
                maintenance,
                currentUser);

        // -----------------------------------------------------
        // Validate status
        // -----------------------------------------------------

        if (maintenance.getStatus() !=
                MaintenanceStatus.RESOLVED) {

            throw new IllegalArgumentException(
                    "Only RESOLVED maintenance requests can be closed.");
        }

        // -----------------------------------------------------
        // Update status
        // -----------------------------------------------------

        maintenance.setStatus(
                MaintenanceStatus.CLOSED);

        MaintenanceRequest updated =
                maintenanceRepository.save(
                        maintenance);

        // -----------------------------------------------------
        // Notify Tenant
        // -----------------------------------------------------

        notifyTenant(
                updated,
                NotificationType.MAINTENANCE_CLOSED,
                "Maintenance Request Closed",
                "Your maintenance request '"
                        + updated.getTitle()
                        + "' for property "
                        + updated.getProperty()
                                .getPropertyName()
                        + " has been closed."
        );
    }

    // =========================================================
    // Owner Authorization
    // =========================================================

    private void checkOwner(
            MaintenanceRequest maintenance,
            User currentUser) {

        if (maintenance.getProperty() == null ||
                maintenance.getProperty().getOwner() == null ||
                !maintenance.getProperty()
                        .getOwner()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to manage this maintenance request.");
        }
    }

    // =========================================================
    // Notify Tenant
    // =========================================================

    private void notifyTenant(
            MaintenanceRequest maintenance,
            NotificationType type,
            String title,
            String message) {

        Tenant tenant =
                maintenance.getTenant();

        if (tenant == null) {
            return;
        }

        User tenantUser =
                userRepository
                        .findByEmail(
                                tenant.getEmail())
                        .orElse(null);

        if (tenantUser == null) {
            return;
        }

        notificationService.createNotification(
                tenantUser.getId(),
                type,
                title,
                message);
    }
}