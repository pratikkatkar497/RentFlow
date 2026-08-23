
package com.RentFlow.service.Impl;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.CreateLeaseRequestDTO;
import com.RentFlow.dto.request.UpdateLeaseRequestDTO;
import com.RentFlow.dto.response.LeaseResponseDTO;
import com.RentFlow.dto.response.PageResponseDTO;
import com.RentFlow.entity.Lease;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.LeaseStatus;
import com.RentFlow.enums.PropertyStatus;
import com.RentFlow.enums.TenantStatus;
import com.RentFlow.exception.AccessDeniedException;
import com.RentFlow.exception.BadRequestException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.LeaseRepository;
import com.RentFlow.repository.PropertyRepository;
import com.RentFlow.repository.TenantRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.LeaseService;

@Service
public class LeaseServiceImpl implements LeaseService {

    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public LeaseServiceImpl(
            LeaseRepository leaseRepository,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            ModelMapper modelMapper,
            PropertyRepository propertyRepository) {

        this.leaseRepository = leaseRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    // =========================================================
    // Get Current Logged-in User
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }

    
    private PageResponseDTO<LeaseResponseDTO> convertToPageResponse(
            Page<Lease> leasePage) {

        PageResponseDTO<LeaseResponseDTO> response =
                new PageResponseDTO<>();

        response.setContent(
                leasePage.getContent()
                        .stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList())
        );

        response.setPageNumber(
                leasePage.getNumber());

        response.setPageSize(
                leasePage.getSize());

        response.setTotalElements(
                leasePage.getTotalElements());

        response.setTotalPages(
                leasePage.getTotalPages());

        response.setFirst(
                leasePage.isFirst());

        response.setLast(
                leasePage.isLast());

        return response;
    }
    
    // =========================================================
    // Create Lease
    // =========================================================

    @Override
    public LeaseResponseDTO createLease(
            CreateLeaseRequestDTO request) {

        User owner = getCurrentUser();

        // Find Property
        Property property =
                propertyRepository.findById(
                        request.getPropertyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Property not found"));

        // Check Property Ownership
        if (!property.getOwner().getId()
                .equals(owner.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to create a lease for this property.");
        }

        // Check Property Availability
        if (property.getStatus()
                != PropertyStatus.AVAILABLE) {

            throw new BadRequestException(
                    "Property is not available for lease.");
        }

        // Find Tenant
        Tenant tenant =
                tenantRepository.findById(
                        request.getTenantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant not found"));

        // Check Tenant belongs to Property
        if (!tenant.getProperty().getId()
                .equals(property.getId())) {

            throw new AccessDeniedException(
                    "Tenant does not belong to this property.");
        }

        // Check Tenant Status
        if (tenant.getStatus()
                != TenantStatus.ACTIVE) {

            throw new BadRequestException(
                    "Only active tenants can have a lease.");
        }

        // Check Date
        if (!request.getEndDate()
                .isAfter(request.getStartDate())) {

            throw new BadRequestException(
                    "End date must be after start date");
        }

        // Check Existing Active Lease
        boolean activeLeaseExists =
                leaseRepository.existsByPropertyAndStatus(
                        property,
                        LeaseStatus.ACTIVE);

        if (activeLeaseExists) {

            throw new BadRequestException(
                    "Property already has an active lease.");
        }

        // Create Lease
        Lease lease = new Lease();

        lease.setProperty(property);
        lease.setTenant(tenant);
        lease.setMonthlyRent(
                request.getMonthlyRent());
        lease.setSecurityDeposit(
                request.getSecurityDeposit());
        lease.setStartDate(
                request.getStartDate());
        lease.setEndDate(
                request.getEndDate());

        // New lease becomes ACTIVE
        lease.setStatus(
                LeaseStatus.ACTIVE);

        Lease savedLease =
                leaseRepository.save(lease);

        // Property becomes RENTED
        property.setStatus(
                PropertyStatus.RENTED);

        propertyRepository.save(property);

        return convertToResponse(savedLease);
    }

    // =========================================================
    // Get All Leases
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<LeaseResponseDTO> getAllLeases(
            int page,
            int size,
            String sortBy,
            String direction,
            LeaseStatus status) {

        User owner = getCurrentUser();

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

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id";
        }

        Sort sort;

        if ("desc".equalsIgnoreCase(direction)) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort);

        Page<Lease> leasePage;

        if (status != null) {

            leasePage =
                    leaseRepository
                            .findByPropertyOwnerAndStatus(
                                    owner,
                                    status,
                                    pageable);

        } else {

            leasePage =
                    leaseRepository
                            .findByPropertyOwner(
                                    owner,
                                    pageable);
        }

        return convertToPageResponse(leasePage);
    }

    // =========================================================
    // Get Lease By ID
    // =========================================================

    @Override
    public LeaseResponseDTO getLeaseById(
            Long leaseId) {

        User owner = getCurrentUser();

        Lease lease =
                leaseRepository.findById(leaseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lease not found"));

        // Ownership Check
        if (!lease.getProperty()
                .getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to access this lease.");
        }

        return convertToResponse(lease);
    }

    // =========================================================
    // Get My Leases
    // =========================================================

    @Override
    public PageResponseDTO<LeaseResponseDTO> getMyLeases(
            int page,
            int size,
            String sortBy,
            String direction) {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository.findByUser(
                        currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant profile not found"));

        if (page < 0) {
            throw new BadRequestException(
                    "Page number cannot be negative");
        }

        if (size <= 0) {
            throw new BadRequestException(
                    "Page size must be greater than zero");
        }

        Sort sort;

        if ("desc".equalsIgnoreCase(direction)) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort);

        Page<Lease> leasePage =
                leaseRepository.findByTenant(
                        tenant,
                        pageable);

        return convertToPageResponse(leasePage);
    }

    // =========================================================
    // Update Lease
    // =========================================================

    @Override
    public LeaseResponseDTO updateLease(
            Long leaseId,
            UpdateLeaseRequestDTO request) {

        User owner = getCurrentUser();

        Lease lease =
                leaseRepository.findById(leaseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lease not found"));

        // Ownership Check
        if (!lease.getProperty()
                .getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to update this lease.");
        }

        // Cannot update terminated lease
        if (lease.getStatus()
                == LeaseStatus.TERMINATED) {

            throw new BadRequestException(
                    "Terminated lease cannot be updated.");
        }

        // Date Validation
        if (!request.getEndDate()
                .isAfter(request.getStartDate())) {

            throw new BadRequestException(
                    "End date must be after start date");
        }

        // Update Fields
        lease.setMonthlyRent(
                request.getMonthlyRent());

        lease.setSecurityDeposit(
                request.getSecurityDeposit());

        lease.setStartDate(
                request.getStartDate());

        lease.setEndDate(
                request.getEndDate());

        Lease updatedLease =
                leaseRepository.save(lease);

        return convertToResponse(updatedLease);
    }

    // =========================================================
    // Activate Lease
    // =========================================================

    @Override
    public LeaseResponseDTO activateLease(
            Long leaseId) {

        User owner = getCurrentUser();

        Lease lease =
                leaseRepository.findById(leaseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lease not found"));

        // Ownership Check
        if (!lease.getProperty()
                .getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to activate this lease.");
        }

        // Tenant must be ACTIVE
        if (lease.getTenant().getStatus()
                != TenantStatus.ACTIVE) {

            throw new BadRequestException(
                    "Inactive tenant cannot have an active lease.");
        }

        // Check another active lease
        boolean activeLeaseExists =
                leaseRepository.existsByPropertyAndStatus(
                        lease.getProperty(),
                        LeaseStatus.ACTIVE);

        if (activeLeaseExists
                && lease.getStatus()
                != LeaseStatus.ACTIVE) {

            throw new BadRequestException(
                    "Property already has an active lease.");
        }

        lease.setStatus(
                LeaseStatus.ACTIVE);

        Lease updatedLease =
                leaseRepository.save(lease);

        // Property becomes RENTED
        Property property =
                lease.getProperty();

        property.setStatus(
                PropertyStatus.RENTED);

        propertyRepository.save(property);

        return convertToResponse(updatedLease);
    }

    // =========================================================
    // Terminate Lease
    // =========================================================

    @Override
    public LeaseResponseDTO terminateLease(
            Long leaseId) {

        User owner = getCurrentUser();

        Lease lease =
                leaseRepository.findById(leaseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lease not found"));

        // Ownership Check
        if (!lease.getProperty()
                .getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to terminate this lease.");
        }

        // Check status
        if (lease.getStatus()
                != LeaseStatus.ACTIVE) {

            throw new BadRequestException(
                    "Only active leases can be terminated.");
        }

        // Terminate Lease
        lease.setStatus(
                LeaseStatus.TERMINATED);

        Lease terminatedLease =
                leaseRepository.save(lease);

        // Property becomes AVAILABLE
        Property property =
                lease.getProperty();

        property.setStatus(
                PropertyStatus.AVAILABLE);

        propertyRepository.save(property);

        return convertToResponse(
                terminatedLease);
    }

    // =========================================================
    // Convert Entity → Response DTO
    // =========================================================

    private LeaseResponseDTO convertToResponse(
            Lease lease) {

        LeaseResponseDTO response =
                new LeaseResponseDTO();

        response.setId(
                lease.getId());

        response.setPropertyId(
                lease.getProperty().getId());

        response.setPropertyName(
                lease.getProperty()
                        .getPropertyName());

        response.setTenantId(
                lease.getTenant().getId());

        response.setTenantName(
                lease.getTenant().getFirstName()
                        + " "
                        + lease.getTenant().getLastName());

        response.setMonthlyRent(
                lease.getMonthlyRent());

        response.setSecurityDeposit(
                lease.getSecurityDeposit());

        response.setStartDate(
                lease.getStartDate());

        response.setEndDate(
                lease.getEndDate());

        response.setStatus(
                lease.getStatus());

        return response;
    }

    // =========================================================
    // Get My Active Lease
    // =========================================================

    @Override
    public LeaseResponseDTO getMyLease() {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository.findByUser(
                        currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant profile not found"));

        Lease lease =
                leaseRepository
                        .findByTenantAndStatus(
                                tenant,
                                LeaseStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active lease not found"));

        return convertToResponse(lease);
    }
}

