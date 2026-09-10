package com.RentFlow.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.CreateOwnerRequestDTO;
import com.RentFlow.dto.request.UpdateOwnerRequestDTO;
import com.RentFlow.dto.response.AdminUserResponseDTO;
import com.RentFlow.entity.Organization;
import com.RentFlow.entity.Role;
import com.RentFlow.entity.User;
import com.RentFlow.enums.RoleType;
import com.RentFlow.exception.BusinessException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.OrganizationRepository;
import com.RentFlow.repository.RoleRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.AdminOwnerService;

@Service
@Transactional
public class AdminOwnerServiceImpl implements AdminOwnerService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final OrganizationRepository organizationRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public AdminOwnerServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			OrganizationRepository organizationRepository, BCryptPasswordEncoder passwordEncoder) {

		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.organizationRepository = organizationRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public AdminUserResponseDTO createOwner(CreateOwnerRequestDTO request) {

		// 1. Validate organization
		Organization organization = organizationRepository.findById(request.getOrganizationId()).orElseThrow(
				() -> new ResourceNotFoundException("Organization not found with id: " + request.getOrganizationId()));

		// 2. Organization must be active
		if (!Boolean.TRUE.equals(organization.getActive())) {
			throw new BusinessException("Cannot create owner for an inactive organization");
		}

		// 3. Check duplicate email
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("Email is already registered");
		}

		// 4. Check duplicate phone
		if (userRepository.existsByPhone(request.getPhone())) {
			throw new BusinessException("Phone number is already registered");
		}

		// 5. Find OWNER role
		Role ownerRole = roleRepository.findByName(RoleType.OWNER)
				.orElseThrow(() -> new ResourceNotFoundException("OWNER role not found"));

		// 6. Create user
		User owner = new User();

		owner.setFirstName(request.getFirstName());
		owner.setLastName(request.getLastName());
		owner.setEmail(request.getEmail());
		owner.setPhone(request.getPhone());

		// Never store plain-text password
		owner.setPassword(passwordEncoder.encode(request.getPassword()));

		owner.setEnabled(true);
		owner.setRole(ownerRole);
		owner.setOrganization(organization);

		// 7. Save
		User savedOwner = userRepository.save(owner);

		// 8. Map response
		return mapToResponse(savedOwner);
	}

	private AdminUserResponseDTO mapToResponse(User user) {

		Long organizationId = null;
		String organizationName = null;

		if (user.getOrganization() != null) {
			organizationId = user.getOrganization().getId();

			organizationName = user.getOrganization().getName();
		}

		String role = null;

		if (user.getRole() != null) {
			role = user.getRole().getName().name();
		}

		return new AdminUserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
				user.getPhone(), role, user.getEnabled(), organizationId, organizationName, user.getCreatedAt(),
				user.getUpdatedAt());
	}

	@Override
	@Transactional(readOnly = true)
	public List<AdminUserResponseDTO> getAllOwners() {

		List<User> owners = userRepository.findByRole_NameOrderByIdDesc(RoleType.OWNER);

		return owners.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public AdminUserResponseDTO getOwnerById(Long id) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));

		if (user.getRole() == null || user.getRole().getName() != RoleType.OWNER) {

			throw new ResourceNotFoundException("Owner not found with id: " + id);
		}

		return mapToResponse(user);
	}

	@Override
	public AdminUserResponseDTO updateOwnerStatus(Long id, boolean enabled) {

		User owner = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));

		if (owner.getRole() == null || owner.getRole().getName() != RoleType.OWNER) {

			throw new ResourceNotFoundException("Owner not found with id: " + id);
		}

		owner.setEnabled(enabled);

		User updatedOwner = userRepository.save(owner);

		return mapToResponse(updatedOwner);
	}
	
	@Override
	public AdminUserResponseDTO updateOwner(
	        Long id,
	        UpdateOwnerRequestDTO request) {

	    User owner = userRepository.findById(id)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Owner not found with id: " + id));

	    if (owner.getRole() == null ||
	            owner.getRole().getName() != RoleType.OWNER) {

	        throw new ResourceNotFoundException(
	                "Owner not found with id: " + id);
	    }

	    if (!owner.getPhone().equals(request.getPhone())
	            && userRepository.existsByPhone(request.getPhone())) {

	        throw new BusinessException(
	                "Phone number is already registered");
	    }

	    owner.setFirstName(request.getFirstName());
	    owner.setLastName(request.getLastName());
	    owner.setPhone(request.getPhone());

	    User updatedOwner = userRepository.save(owner);

	    return mapToResponse(updatedOwner);
	}
}