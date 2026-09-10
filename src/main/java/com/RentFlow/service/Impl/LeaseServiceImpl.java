
package com.RentFlow.service.Impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
import com.RentFlow.dto.request.LeaseFilterRequestDTO;
import com.RentFlow.dto.request.UpdateLeaseRequestDTO;
import com.RentFlow.dto.response.LeaseResponseDTO;
import com.RentFlow.dto.response.PageResponseDTO;
import com.RentFlow.entity.Lease;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.LeaseStatus;
import com.RentFlow.enums.NotificationType;
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
import com.RentFlow.service.NotificationService;
import com.RentFlow.specification.LeaseSpecification;

@Service
public class LeaseServiceImpl implements LeaseService {

    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

    public LeaseServiceImpl(
            LeaseRepository leaseRepository,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            ModelMapper modelMapper,
            PropertyRepository propertyRepository,
            NotificationService notificationService) {

        this.leaseRepository = leaseRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.notificationService = notificationService;
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
    @Transactional
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

        // Notify tenant
        if (tenant.getUser() != null) {

            notificationService.createNotification(
                    tenant.getUser().getId(),
                    NotificationType.LEASE_CREATED,
                    "Lease Created",
                    "A new lease has been created for property "
                            + property.getPropertyName()
                            + "."
            );
        }

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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
        if (size > 100) {
            throw new BadRequestException(
                    "Page size cannot be greater than 100");
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
    @Transactional
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
        
        if (request.getMonthlyRent() == null ||
                request.getMonthlyRent().compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Monthly rent must be greater than zero.");
        }

        if (request.getSecurityDeposit() == null ||
                request.getSecurityDeposit().compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException(
                    "Security deposit cannot be negative.");
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
    @Transactional
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

     // Notify tenant
     Tenant tenant = lease.getTenant();

     if (tenant.getUser() != null) {

         notificationService.createNotification(
                 tenant.getUser().getId(),
                 NotificationType.LEASE_ACTIVATED,
                 "Lease Activated",
                 "Your lease for property "
                         + property.getPropertyName()
                         + " has been activated."
         );
     }

     return convertToResponse(updatedLease);
    }

    // =========================================================
    // Terminate Lease
    // =========================================================

    @Override
    @Transactional
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

     // Notify tenant
     Tenant tenant = lease.getTenant();

     if (tenant.getUser() != null) {

         notificationService.createNotification(
                 tenant.getUser().getId(),
                 NotificationType.LEASE_TERMINATED,
                 "Lease Terminated",
                 "Your lease for property "
                         + property.getPropertyName()
                         + " has been terminated."
         );
     }

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
    @Transactional(readOnly = true)
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
    
 // =========================================================
 // SEARCH + FILTER + PAGINATION + SORTING
 // =========================================================

 @Override
 @Transactional(readOnly = true)
 public PageResponseDTO<LeaseResponseDTO> filterLeases(
         LeaseFilterRequestDTO request,
         int page,
         int size,
         String sortBy,
         String direction) {

     User owner = getCurrentUser();

     // =========================================================
     // Validate Pagination
     // =========================================================

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

     // =========================================================
     // Default Sorting
     // =========================================================

     if (sortBy == null ||
    	        sortBy.trim().isEmpty()) {

    	    sortBy = "id";
    	}

    	validateLeaseSortField(sortBy);

     if (direction == null ||
             direction.trim().isEmpty()) {

         direction = "desc";
     }

     Sort sort;

     if ("desc".equalsIgnoreCase(direction)) {

         sort = Sort.by(sortBy)
                 .descending();

     } else if ("asc".equalsIgnoreCase(direction)) {

         sort = Sort.by(sortBy)
                 .ascending();

     } else {

         throw new BadRequestException(
                 "Sort direction must be 'asc' or 'desc'");
     }

     Pageable pageable =
             PageRequest.of(
                     page,
                     size,
                     sort);

     // =========================================================
     // Validate Rent Range
     // =========================================================

     if (request.getMinRent() != null &&
             request.getMaxRent() != null) {

         if (request.getMinRent()
                 .compareTo(
                         request.getMaxRent()) > 0) {

             throw new BadRequestException(
                     "Minimum rent cannot be greater than maximum rent");
         }
     }

     // =========================================================
     // Validate Start Date Range
     // =========================================================

     if (request.getStartDateFrom() != null &&
             request.getStartDateTo() != null) {

         if (request.getStartDateFrom()
                 .isAfter(
                         request.getStartDateTo())) {

             throw new BadRequestException(
                     "Start date from cannot be after start date to");
         }
     }

     // =========================================================
     // Validate End Date Range
     // =========================================================

     if (request.getEndDateFrom() != null &&
             request.getEndDateTo() != null) {

         if (request.getEndDateFrom()
                 .isAfter(
                         request.getEndDateTo())) {

             throw new BadRequestException(
                     "End date from cannot be after end date to");
         }
     }

     
     // =========================================================
     // Build Specification
     // =========================================================

     Page<Lease> leasePage =
             leaseRepository.findAll(
                     LeaseSpecification.filterLeases(
                             owner,
                             request.getSearch(),
                             request.getPropertyId(),
                             request.getTenantId(),
                             request.getStatus(),
                             request.getMinRent(),
                             request.getMaxRent(),
                             request.getStartDateFrom(),
                             request.getStartDateTo(),
                             request.getEndDateFrom(),
                             request.getEndDateTo()),
                     pageable);

     // =========================================================
     // Convert Page
     // =========================================================

     return convertToPageResponse(
             leasePage);
 }
 
 private void validateLeaseSortField(
	        String sortBy) {

	    if (!sortBy.equals("id")
	            && !sortBy.equals("monthlyRent")
	            && !sortBy.equals("securityDeposit")
	            && !sortBy.equals("startDate")
	            && !sortBy.equals("endDate")
	            && !sortBy.equals("status")) {

	        throw new BadRequestException(
	                "Invalid lease sort field: " + sortBy);
	    }
	}

//=========================================================
//AUTOMATIC LEASE EXPIRY
//=========================================================

@Override
@Transactional
public void updateExpiredLeases() {

  LocalDate today = LocalDate.now();

  // Find all ACTIVE leases whose end date has passed
  List<Lease> expiredLeases =
          leaseRepository.findByStatusAndEndDateBefore(
                  LeaseStatus.ACTIVE,
                  today);

  for (Lease lease : expiredLeases) {

      // Change lease status to EXPIRED
      lease.setStatus(LeaseStatus.EXPIRED);

      // Make the property available again
      Property property = lease.getProperty();

      if (property != null
              && property.getStatus() == PropertyStatus.RENTED) {

          property.setStatus(PropertyStatus.AVAILABLE);

          propertyRepository.save(property);
      }

      leaseRepository.save(lease);
  }
}

//=========================================================
//LEASE EXPIRY NOTIFICATIONS
//=========================================================

@Override
@Transactional
public void sendLeaseExpiryNotifications() {

 LocalDate today = LocalDate.now();

 List<Lease> activeLeases =
         leaseRepository.findByStatus(
                 LeaseStatus.ACTIVE);

 for (Lease lease : activeLeases) {

     if (lease.getEndDate() == null) {
         continue;
     }

     long daysRemaining =
             java.time.temporal.ChronoUnit.DAYS.between(
                     today,
                     lease.getEndDate());

     // Send notification only at 30, 7 and 1 day before expiry
     if (daysRemaining != 30
             && daysRemaining != 7
             && daysRemaining != 1) {
         continue;
     }

     Tenant tenant = lease.getTenant();

     if (tenant == null || tenant.getUser() == null) {
         continue;
     }

     Long userId = tenant.getUser().getId();

     String propertyName =
             lease.getProperty().getPropertyName();

     String message;

     if (daysRemaining == 1) {

         message = "Your lease for property "
                 + propertyName
                 + " expires tomorrow on "
                 + lease.getEndDate()
                 + ".";

     } else {

         message = "Your lease for property "
                 + propertyName
                 + " expires in "
                 + daysRemaining
                 + " days on "
                 + lease.getEndDate()
                 + ".";
     }

     // Prevent duplicate notifications
     boolean alreadySent =
             notificationService.notificationExists(
                     userId,
                     NotificationType.LEASE_EXPIRING,
                     message);

     if (alreadySent) {
         continue;
     }

     notificationService.createNotification(
             userId,
             NotificationType.LEASE_EXPIRING,
             "Lease Expiring",
             message);
 }
}
}

