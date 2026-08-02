package com.RentFlow.service.Impl;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.RentFlow.dto.request.CreateTenantRequestDTO;
import com.RentFlow.dto.request.UpdateTenantRequestDTO;
import com.RentFlow.dto.response.TenantResponseDTO;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.PropertyStatus;
import com.RentFlow.enums.TenantStatus;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.PropertyRepository;
import com.RentFlow.repository.TenantRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.TenantService;



@Service
public class TenantServiceImpl implements TenantService {

	private final TenantRepository tenantRepository;
	private final PropertyRepository propertyRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	public TenantServiceImpl(
	        TenantRepository tenantRepository,
	        PropertyRepository propertyRepository,
	        UserRepository userRepository,
	        ModelMapper modelMapper) {

	    this.tenantRepository = tenantRepository;
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
	public TenantResponseDTO createTenant(CreateTenantRequestDTO request) {

	    if (tenantRepository.existsByEmail(request.getEmail())) {
	        throw new RuntimeException("Email already exists");
	    }

	    if (tenantRepository.existsByPhone(request.getPhone())) {
	        throw new RuntimeException("Phone already exists");
	    }

	    User currentUser = getCurrentUser();

	    Property property = propertyRepository
	            .findByIdAndOwner(
	                    request.getPropertyId(),
	                    currentUser)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Property not found"));

	    if (tenantRepository.existsByPropertyAndStatus(
	            property,
	            TenantStatus.ACTIVE)) {

	        throw new RuntimeException(
	                "Property already occupied");
	    }

	    Tenant tenant = new Tenant();

	    tenant.setFirstName(request.getFirstName());
	    tenant.setLastName(request.getLastName());
	    tenant.setEmail(request.getEmail());
	    tenant.setPhone(request.getPhone());
	    tenant.setGender(request.getGender());
	    tenant.setOccupation(request.getOccupation());
	    tenant.setCompanyName(request.getCompanyName());
	    tenant.setAadhaarNumber(request.getAadhaarNumber());
	    tenant.setPermanentAddress(request.getPermanentAddress());
	    tenant.setEmergencyContact(request.getEmergencyContact());
	    tenant.setMoveInDate(request.getMoveInDate());

	    tenant.setStatus(TenantStatus.ACTIVE);

	    tenant.setProperty(property);

	    property.setStatus(PropertyStatus.OCCUPIED);

	    propertyRepository.save(property);

	    Tenant savedTenant =
	            tenantRepository.save(tenant);

	    TenantResponseDTO response =
	            modelMapper.map(savedTenant,
	                    TenantResponseDTO.class);

	    response.setPropertyId(property.getId());
	    response.setPropertyName(property.getPropertyName());

	    return response;
	}

	@Override
	public List<TenantResponseDTO> getAllTenants() {

	    User currentUser = getCurrentUser();

	    List<Tenant> tenants =
	            tenantRepository.findByPropertyOwner(currentUser);

	    return tenants.stream().map(tenant -> {

	        TenantResponseDTO dto =
	                modelMapper.map(tenant,
	                        TenantResponseDTO.class);

	        dto.setPropertyId(
	                tenant.getProperty().getId());

	        dto.setPropertyName(
	                tenant.getProperty().getPropertyName());

	        return dto;

	    }).collect(Collectors.toList());
	}

	@Override
	public TenantResponseDTO getTenantById(Long tenantId) {

	    User currentUser = getCurrentUser();

	    Tenant tenant =
	            tenantRepository.findByIdAndPropertyOwner(
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
	            tenant.getProperty().getPropertyName());

	    return dto;
	}

	@Override
	public TenantResponseDTO updateTenant(
	        Long tenantId,
	        UpdateTenantRequestDTO request) {

	    User currentUser = getCurrentUser();

	    Tenant tenant =
	            tenantRepository.findByIdAndPropertyOwner(
	                    tenantId,
	                    currentUser)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Tenant not found"));

	    if (!tenant.getEmail().equals(request.getEmail())
	            && tenantRepository.existsByEmail(request.getEmail())) {

	        throw new RuntimeException("Email already exists");
	    }

	    if (!tenant.getPhone().equals(request.getPhone())
	            && tenantRepository.existsByPhone(request.getPhone())) {

	        throw new RuntimeException("Phone already exists");
	    }

	    tenant.setFirstName(request.getFirstName());
	    tenant.setLastName(request.getLastName());
	    tenant.setEmail(request.getEmail());
	    tenant.setPhone(request.getPhone());
	    tenant.setGender(request.getGender());
	    tenant.setOccupation(request.getOccupation());
	    tenant.setCompanyName(request.getCompanyName());
	    tenant.setAadhaarNumber(request.getAadhaarNumber());
	    tenant.setPermanentAddress(request.getPermanentAddress());
	    tenant.setEmergencyContact(request.getEmergencyContact());
	    tenant.setMoveInDate(request.getMoveInDate());

	    Tenant updated =
	            tenantRepository.save(tenant);

	    TenantResponseDTO dto =
	            modelMapper.map(updated,
	                    TenantResponseDTO.class);

	    dto.setPropertyId(updated.getProperty().getId());
	    dto.setPropertyName(updated.getProperty().getPropertyName());

	    return dto;
	}

	@Override
	public void deactivateTenant(Long tenantId) {

	    User currentUser = getCurrentUser();

	    Tenant tenant =
	            tenantRepository.findByIdAndPropertyOwner(
	                    tenantId,
	                    currentUser)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Tenant not found"));

	    tenant.setStatus(TenantStatus.INACTIVE);
	    tenant.setMoveOutDate(LocalDate.now());

	    Property property = tenant.getProperty();

	    property.setStatus(PropertyStatus.AVAILABLE);

	    propertyRepository.save(property);

	    tenantRepository.save(tenant);
	}

}