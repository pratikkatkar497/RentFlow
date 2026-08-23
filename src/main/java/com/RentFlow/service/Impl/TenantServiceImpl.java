package com.RentFlow.service.Impl;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.CreateTenantRequestDTO;
import com.RentFlow.dto.request.TenantFilterRequestDTO;
import com.RentFlow.dto.request.UpdateMyTenantRequestDTO;
import com.RentFlow.dto.request.UpdateTenantRequestDTO;
import com.RentFlow.dto.response.PageResponseDTO;
import com.RentFlow.dto.response.PropertyResponseDTO;
import com.RentFlow.dto.response.TenantResponseDTO;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.Role;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.PropertyStatus;
import com.RentFlow.enums.RoleType;
import com.RentFlow.enums.TenantStatus;
import com.RentFlow.exception.BadRequestException;
import com.RentFlow.exception.DuplicateResourceException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.PropertyRepository;
import com.RentFlow.repository.RoleRepository;
import com.RentFlow.repository.TenantRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.TenantService;
import com.RentFlow.specification.TenantSpecification;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public TenantServiceImpl(
            TenantRepository tenantRepository,
            PropertyRepository propertyRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            ModelMapper modelMapper) {

        this.tenantRepository = tenantRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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

    // =========================================================
    // Create Tenant
    // =========================================================

    @Override
    public TenantResponseDTO createTenant(
            CreateTenantRequestDTO request) {

        // 1. Get logged-in OWNER
        User currentUser = getCurrentUser();

        // 2. Check Tenant email
        if (tenantRepository.existsByEmail(
                request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already exists");
        }

        // 3. Check User email
        if (userRepository.findByEmail(
                request.getEmail()).isPresent()) {

            throw new DuplicateResourceException(
                    "User email already exists");
        }

        // 4. Check Tenant phone
        if (tenantRepository.existsByPhone(
                request.getPhone())) {

            throw new DuplicateResourceException(
                    "Phone already exists");
        }

        // 5. Check User phone
        if (userRepository.existsByPhone(
                request.getPhone())) {

            throw new DuplicateResourceException(
                    "Phone already exists");
        }

        // 6. Find property owned by current OWNER
        Property property =
                propertyRepository.findByIdAndOwner(
                        request.getPropertyId(),
                        currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Property not found"));

        // 7. Check property availability
        if (property.getStatus()
                != PropertyStatus.AVAILABLE) {

            throw new BadRequestException(
                    "Property is not available");
        }

        // 8. Find TENANT role
        Role tenantRole =
                roleRepository.findByName(
                        RoleType.TENANT)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "TENANT role not found"));

        // 9. Create User account
        User tenantUser = new User();

        tenantUser.setFirstName(
                request.getFirstName());

        tenantUser.setLastName(
                request.getLastName());

        tenantUser.setEmail(
                request.getEmail());

        tenantUser.setPhone(
                request.getPhone());

        tenantUser.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));

        tenantUser.setRole(tenantRole);

        tenantUser.setEnabled(true);

        User savedUser =
                userRepository.save(tenantUser);

        // 10. Create Tenant profile
        Tenant tenant = new Tenant();

        tenant.setFirstName(
                request.getFirstName());

        tenant.setLastName(
                request.getLastName());

        tenant.setEmail(
                request.getEmail());

        tenant.setPhone(
                request.getPhone());

        tenant.setGender(
                request.getGender());

        tenant.setOccupation(
                request.getOccupation());

        tenant.setCompanyName(
                request.getCompanyName());

        tenant.setAadhaarNumber(
                request.getAadhaarNumber());

        tenant.setPermanentAddress(
                request.getPermanentAddress());

        tenant.setEmergencyContact(
                request.getEmergencyContact());

        tenant.setMoveInDate(
                request.getMoveInDate());

        tenant.setStatus(
                TenantStatus.ACTIVE);

        tenant.setProperty(property);

        // Link Tenant with User
        tenant.setUser(savedUser);

        // 11. Save Tenant
        Tenant savedTenant =
                tenantRepository.save(tenant);

        // 12. Create response
        TenantResponseDTO response =
                modelMapper.map(
                        savedTenant,
                        TenantResponseDTO.class);

        response.setPropertyId(
                property.getId());

        response.setPropertyName(
                property.getPropertyName());

        return response;
    }

    private PageResponseDTO<TenantResponseDTO> convertToPageResponse(
            Page<Tenant> tenantPage) {

        PageResponseDTO<TenantResponseDTO> response =
                new PageResponseDTO<>();

        response.setContent(
                tenantPage.getContent()
                        .stream()
                        .map(tenant -> {

                            TenantResponseDTO dto =
                                    modelMapper.map(
                                            tenant,
                                            TenantResponseDTO.class);

                            if (tenant.getProperty() != null) {

                                dto.setPropertyId(
                                        tenant.getProperty().getId());

                                dto.setPropertyName(
                                        tenant.getProperty()
                                                .getPropertyName());
                            }

                            return dto;
                        })
                        .collect(Collectors.toList())
        );

        response.setPageNumber(
                tenantPage.getNumber());

        response.setPageSize(
                tenantPage.getSize());

        response.setTotalElements(
                tenantPage.getTotalElements());

        response.setTotalPages(
                tenantPage.getTotalPages());

        response.setFirst(
                tenantPage.isFirst());

        response.setLast(
                tenantPage.isLast());

        return response;
    }
    
    // =========================================================
    // Get All Tenants
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<TenantResponseDTO> getAllTenants(
            int page,
            int size,
            String sortBy,
            String direction,
            TenantStatus status) {

        User currentUser = getCurrentUser();

        // =========================================================
        // Validate pagination
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
        // Default sorting
        // =========================================================

        if (sortBy == null ||
                sortBy.trim().isEmpty()) {

            sortBy = "id";
        }

        Sort sort;

        if ("desc".equalsIgnoreCase(direction)) {

            sort = Sort.by(sortBy)
                    .descending();

        } else {

            sort = Sort.by(sortBy)
                    .ascending();
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort);

        // =========================================================
        // Fetch data
        // =========================================================

        Page<Tenant> tenantPage;

        if (status != null) {

            tenantPage =
                    tenantRepository
                            .findByPropertyOwnerAndStatus(
                                    currentUser,
                                    status,
                                    pageable);

        } else {

            tenantPage =
                    tenantRepository
                            .findByPropertyOwner(
                                    currentUser,
                                    pageable);
        }

        // =========================================================
        // Convert Page → PageResponseDTO
        // =========================================================

        return convertToPageResponse(
                tenantPage);
    }
    // =========================================================
    // Get Tenant By ID
    // =========================================================

    @Override
    public TenantResponseDTO getTenantById(
            Long tenantId) {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository
                        .findByIdAndPropertyOwner(
                                tenantId,
                                currentUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found"));

        TenantResponseDTO dto =
                modelMapper.map(
                        tenant,
                        TenantResponseDTO.class);

        dto.setPropertyId(
                tenant.getProperty().getId());

        dto.setPropertyName(
                tenant.getProperty()
                        .getPropertyName());

        return dto;
    }

    // =========================================================
    // Update Tenant
    // =========================================================

    @Override
    public TenantResponseDTO updateTenant(
            Long tenantId,
            UpdateTenantRequestDTO request) {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository
                        .findByIdAndPropertyOwner(
                                tenantId,
                                currentUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found"));

        // Check email uniqueness
        if (!tenant.getEmail().equals(
                request.getEmail())
                && tenantRepository.existsByEmail(
                        request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already exists");
        }

        // Check phone uniqueness
        if (!tenant.getPhone().equals(
                request.getPhone())
                && tenantRepository.existsByPhone(
                        request.getPhone())) {

            throw new DuplicateResourceException(
                    "Phone already exists");
        }

        tenant.setFirstName(
                request.getFirstName());

        tenant.setLastName(
                request.getLastName());

        tenant.setEmail(
                request.getEmail());

        tenant.setPhone(
                request.getPhone());

        tenant.setGender(
                request.getGender());

        tenant.setOccupation(
                request.getOccupation());

        tenant.setCompanyName(
                request.getCompanyName());

        tenant.setAadhaarNumber(
                request.getAadhaarNumber());

        tenant.setPermanentAddress(
                request.getPermanentAddress());

        tenant.setEmergencyContact(
                request.getEmergencyContact());

        tenant.setMoveInDate(
                request.getMoveInDate());

        // Also update linked User
        User tenantUser = tenant.getUser();

        if (tenantUser != null) {

            tenantUser.setFirstName(
                    request.getFirstName());

            tenantUser.setLastName(
                    request.getLastName());

            tenantUser.setEmail(
                    request.getEmail());

            tenantUser.setPhone(
                    request.getPhone());

            userRepository.save(tenantUser);
        }

        Tenant updated =
                tenantRepository.save(tenant);

        TenantResponseDTO dto =
                modelMapper.map(
                        updated,
                        TenantResponseDTO.class);

        dto.setPropertyId(
                updated.getProperty().getId());

        dto.setPropertyName(
                updated.getProperty()
                        .getPropertyName());

        return dto;
    }

    // =========================================================
    // Get My Profile
    // =========================================================

    @Override
    public TenantResponseDTO getMyProfile() {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository.findByEmail(
                        currentUser.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant profile not found"));

        TenantResponseDTO response =
                modelMapper.map(
                        tenant,
                        TenantResponseDTO.class);

        if (tenant.getProperty() != null) {

            response.setPropertyId(
                    tenant.getProperty().getId());

            response.setPropertyName(
                    tenant.getProperty()
                            .getPropertyName());
        }

        return response;
    }

    // =========================================================
    // Deactivate Tenant
    // =========================================================

    @Override
    public void deactivateTenant(
            Long tenantId) {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository
                        .findByIdAndPropertyOwner(
                                tenantId,
                                currentUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found"));

        tenant.setStatus(
                TenantStatus.INACTIVE);

        tenant.setMoveOutDate(
                LocalDate.now());

        Property property =
                tenant.getProperty();

        property.setStatus(
                PropertyStatus.AVAILABLE);

        // Disable login
        User tenantUser =
                tenant.getUser();

        if (tenantUser != null) {

            tenantUser.setEnabled(false);

            userRepository.save(tenantUser);
        }

        propertyRepository.save(property);

        tenantRepository.save(tenant);
    }

    // =========================================================
    // Get My Property
    // =========================================================

    @Override
    public PropertyResponseDTO getMyProperty() {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository.findByUser(
                        currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant profile not found"));

        Property property =
                tenant.getProperty();

        if (property == null) {

            throw new ResourceNotFoundException(
                    "Property not found for tenant");
        }

        PropertyResponseDTO response =
                modelMapper.map(
                        property,
                        PropertyResponseDTO.class);

        response.setOwnerId(
                property.getOwner().getId());

        response.setOwnerName(
                property.getOwner().getFirstName()
                        + " "
                        + property.getOwner().getLastName());

        return response;
    }

    // =========================================================
    // Update My Profile
    // =========================================================

    @Override
    public TenantResponseDTO updateMyProfile(
            UpdateMyTenantRequestDTO request) {

        User currentUser = getCurrentUser();

        Tenant tenant =
                tenantRepository.findByEmail(
                        currentUser.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant profile not found"));

        // Check phone uniqueness
        if (!tenant.getPhone().equals(
                request.getPhone())
                && tenantRepository.existsByPhone(
                        request.getPhone())) {

            throw new DuplicateResourceException(
                    "Phone number already exists");
        }

        tenant.setFirstName(
                request.getFirstName());

        tenant.setLastName(
                request.getLastName());

        tenant.setPhone(
                request.getPhone());

        tenant.setGender(
                request.getGender());

        tenant.setOccupation(
                request.getOccupation());

        tenant.setCompanyName(
                request.getCompanyName());

        tenant.setPermanentAddress(
                request.getPermanentAddress());

        tenant.setEmergencyContact(
                request.getEmergencyContact());

        Tenant updated =
                tenantRepository.save(tenant);

        TenantResponseDTO response =
                modelMapper.map(
                        updated,
                        TenantResponseDTO.class);

        if (updated.getProperty() != null) {

            response.setPropertyId(
                    updated.getProperty().getId());

            response.setPropertyName(
                    updated.getProperty()
                            .getPropertyName());
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<TenantResponseDTO> filterTenants(
            TenantFilterRequestDTO request,
            int page,
            int size,
            String sortBy,
            String direction) {

        User currentUser = getCurrentUser();

        // =========================================================
        // Validate pagination
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
        // Default sorting
        // =========================================================

        if (sortBy == null ||
                sortBy.trim().isEmpty()) {

            sortBy = "id";
        }

        Sort sort;

        if ("desc".equalsIgnoreCase(direction)) {

            sort = Sort.by(sortBy)
                    .descending();

        } else {

            sort = Sort.by(sortBy)
                    .ascending();
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort);

        // =========================================================
        // Dynamic filtering
        // =========================================================

        Page<Tenant> tenantPage =
                tenantRepository.findAll(
                        TenantSpecification.filterTenants(
                                currentUser,
                                request.getSearch(),
                                request.getStatus(),
                                request.getOccupation(),
                                request.getCompanyName()),
                        pageable);

        // =========================================================
        // Response
        // =========================================================

        return convertToPageResponse(
                tenantPage);
    }
}

