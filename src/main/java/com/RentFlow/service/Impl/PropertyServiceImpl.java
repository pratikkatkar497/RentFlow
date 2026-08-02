package com.RentFlow.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.RentFlow.dto.request.PropertyRequestDTO;
import com.RentFlow.dto.response.PropertyResponseDTO;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.User;
import com.RentFlow.enums.PropertyStatus;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.PropertyRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.PropertyService;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public PropertyServiceImpl(
            PropertyRepository propertyRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {

        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }
    
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    @Override
    public PropertyResponseDTO createProperty(PropertyRequestDTO request) {

    	User owner = getCurrentUser();

        // Convert DTO to Entity
        Property property = modelMapper.map(request, Property.class);

        // Set owner
        property.setOwner(owner);

        // Default status
        property.setStatus(PropertyStatus.AVAILABLE);

        // Save property
        Property savedProperty = propertyRepository.save(property);

        // Convert Entity to Response DTO
        PropertyResponseDTO response =
                modelMapper.map(savedProperty, PropertyResponseDTO.class);

        response.setOwnerId(owner.getId());

        response.setOwnerName(
                owner.getFirstName() + " " + owner.getLastName());

        return response;
    }

    @Override
    public PropertyResponseDTO getPropertyById(Long id) {

        User currentUser = getCurrentUser();

        Property property = propertyRepository
                .findByIdAndOwner(id, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Property not found"));

        PropertyResponseDTO response =
                modelMapper.map(property, PropertyResponseDTO.class);

        response.setOwnerId(currentUser.getId());
        response.setOwnerName(
                currentUser.getFirstName() + " " +
                currentUser.getLastName());

        return response;
    }

    @Override
    public List<PropertyResponseDTO> getMyProperties() {

        User currentUser = getCurrentUser();

        List<Property> properties =
                propertyRepository.findByOwner(currentUser);

        return properties.stream()
                .map(property -> {

                    PropertyResponseDTO response =
                            modelMapper.map(property, PropertyResponseDTO.class);

                    response.setOwnerId(currentUser.getId());

                    response.setOwnerName(
                            currentUser.getFirstName() + " " +
                            currentUser.getLastName());

                    return response;

                })
                .collect(Collectors.toList());
    }

    @Override
    public PropertyResponseDTO updateProperty(Long id, PropertyRequestDTO request) {

        // Get logged-in user's email
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // Find logged-in user
        User currentUser = getCurrentUser();

        Property property = propertyRepository
                .findByIdAndOwner(id, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Property not found"));

        // Update editable fields
        property.setPropertyName(request.getPropertyName());
        property.setDescription(request.getDescription());
        property.setPropertyType(request.getPropertyType());
        property.setAddressLine1(request.getAddressLine1());
        property.setAddressLine2(request.getAddressLine2());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setCountry(request.getCountry());
        property.setPostalCode(request.getPostalCode());

        property.setMonthlyRent(request.getMonthlyRent());
        property.setSecurityDeposit(request.getSecurityDeposit());
        property.setMaintenanceCharge(request.getMaintenanceCharge());

        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setArea(request.getArea());

        property.setParkingAvailable(request.getParkingAvailable());
        property.setBalconyAvailable(request.getBalconyAvailable());

        // Save updated property
        Property updatedProperty = propertyRepository.save(property);

        // Convert to response DTO
        PropertyResponseDTO response =
                modelMapper.map(updatedProperty, PropertyResponseDTO.class);

        response.setOwnerId(currentUser.getId());
        response.setOwnerName(
                currentUser.getFirstName() + " " +
                currentUser.getLastName());

        return response;
    }

    @Override
    public void deleteProperty(Long id) {

        User currentUser = getCurrentUser();

        Property property = propertyRepository
                .findByIdAndOwner(id, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Property not found"));

        propertyRepository.delete(property);
    }

}