package com.RentFlow.service.Impl;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.PropertyFilterRequestDTO;
import com.RentFlow.dto.request.PropertyRequestDTO;
import com.RentFlow.dto.response.PropertyResponseDTO;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.User;
import com.RentFlow.enums.PropertyStatus;
import com.RentFlow.exception.BadRequestException;
import com.RentFlow.exception.DuplicateResourceException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.PropertyRepository;
import com.RentFlow.repository.TenantRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.PropertyService;
import com.RentFlow.service.SubscriptionLimitService;
import com.RentFlow.specification.PropertySpecification;

@Service
public class PropertyServiceImpl
        implements PropertyService {

    private final PropertyRepository propertyRepository;

    private final UserRepository userRepository;
    private final SubscriptionLimitService subscriptionLimitService;
    private final ModelMapper modelMapper;
    private final TenantRepository tenantRepository;

    public PropertyServiceImpl(
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            SubscriptionLimitService subscriptionLimitService, ModelMapper modelMapper) {

        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.subscriptionLimitService =subscriptionLimitService;
		this.modelMapper = modelMapper;
		  this.tenantRepository = tenantRepository;
    }

    // =========================================================
    // Get Current User
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }

    // =========================================================
    // Create Property
    // =========================================================

    @Transactional
    @Override
    public PropertyResponseDTO createProperty(
            PropertyRequestDTO request) {

        User owner = getCurrentUser();

        // Check whether organization's subscription is active
        subscriptionLimitService.checkSubscriptionActive(
                owner.getOrganization().getId());

        // Check subscription property limit
        subscriptionLimitService.checkPropertyLimit(
                owner.getOrganization().getId());

        Property property =
                modelMapper.map(
                        request,
                        Property.class);

        // Set owner
        property.setOwner(owner);

        // Default status
        property.setStatus(
                PropertyStatus.AVAILABLE);

        Property savedProperty =
                propertyRepository.save(property);

        return convertToResponse(
                savedProperty,
                owner);
    }

    // =========================================================
    // Get Property By ID
    // =========================================================

    @Override
    public PropertyResponseDTO getPropertyById(
            Long id) {

        User currentUser =
                getCurrentUser();

        Property property =
                propertyRepository
                        .findByIdAndOwner(
                                id,
                                currentUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));

        return convertToResponse(
                property,
                currentUser);
    }

    // =========================================================
    // Get My Properties - PAGINATION
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyResponseDTO> getMyProperties(
            Pageable pageable) {

        User currentUser = getCurrentUser();

        Page<Property> properties =
                propertyRepository.findByOwner(
                        currentUser,
                        pageable);

        return properties.map(property -> {

            PropertyResponseDTO response =
                    modelMapper.map(
                            property,
                            PropertyResponseDTO.class);

            response.setOwnerId(
                    currentUser.getId());

            response.setOwnerName(
                    currentUser.getFirstName()
                            + " "
                            + currentUser.getLastName());

            return response;
        });
    }

    // =========================================================
    // Update Property
    // =========================================================

    @Transactional
    @Override
    public PropertyResponseDTO updateProperty(
            Long id,
            PropertyRequestDTO request) {

        User currentUser =
                getCurrentUser();

        Property property =
                propertyRepository
                        .findByIdAndOwner(
                                id,
                                currentUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));

        // -----------------------------------------------------
        // Update fields
        // -----------------------------------------------------

        property.setPropertyName(
                request.getPropertyName());

        property.setDescription(
                request.getDescription());

        property.setPropertyType(
                request.getPropertyType());

        property.setAddressLine1(
                request.getAddressLine1());

        property.setAddressLine2(
                request.getAddressLine2());

        property.setCity(
                request.getCity());

        property.setState(
                request.getState());

        property.setCountry(
                request.getCountry());

        property.setPostalCode(
                request.getPostalCode());

        property.setMonthlyRent(
                request.getMonthlyRent());

        property.setSecurityDeposit(
                request.getSecurityDeposit());

        property.setMaintenanceCharge(
                request.getMaintenanceCharge());

        property.setBedrooms(
                request.getBedrooms());

        property.setBathrooms(
                request.getBathrooms());

        property.setArea(
                request.getArea());

        property.setParkingAvailable(
                request.getParkingAvailable());

        property.setBalconyAvailable(
                request.getBalconyAvailable());

        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        Property updatedProperty =
                propertyRepository.save(property);

        return convertToResponse(
                updatedProperty,
                currentUser);
    }

    // =========================================================
    // Delete Property
    // =========================================================

    @Transactional
    @Override
    public void deleteProperty(
            Long id) {

        User currentUser =
                getCurrentUser();

        Property property =
                propertyRepository
                        .findByIdAndOwner(
                                id,
                                currentUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));
        if (tenantRepository.existsByProperty(property)) {
        	throw new DuplicateResourceException(
        		    "Property cannot be deleted because it has an associated tenant."
        		);
        }

        propertyRepository.delete(property);
    }

    // =========================================================
    // Entity → Response DTO
    // =========================================================

    private PropertyResponseDTO convertToResponse(
            Property property,
            User owner) {

        PropertyResponseDTO response =
                modelMapper.map(
                        property,
                        PropertyResponseDTO.class);

        response.setOwnerId(
                owner.getId());

        response.setOwnerName(
                owner.getFirstName()
                        + " "
                        + owner.getLastName());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyResponseDTO> filterProperties(
            PropertyFilterRequestDTO request,
            Pageable pageable) {

        User currentUser = getCurrentUser();

        if (request.getMinRent() != null
                && request.getMaxRent() != null
                && request.getMinRent()
                        .compareTo(request.getMaxRent()) > 0) {

            throw new BadRequestException(
                    "Minimum rent cannot be greater than maximum rent");
        }

        Page<Property> properties =
                propertyRepository.findAll(
                        PropertySpecification.filterProperties(
                                currentUser,
                                request.getSearch(),
                                request.getCity(),
                                request.getState(),
                                request.getPropertyType(),
                                request.getStatus(),
                                request.getMinRent(),
                                request.getMaxRent()),
                        pageable);

        return properties.map(property ->
                convertToResponse(
                        property,
                        currentUser));
    }
}