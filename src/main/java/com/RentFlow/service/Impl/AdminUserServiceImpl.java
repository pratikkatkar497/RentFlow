package com.RentFlow.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.response.AdminUserResponseDTO;
import com.RentFlow.entity.Organization;
import com.RentFlow.entity.User;
import com.RentFlow.enums.RoleType;
import com.RentFlow.exception.BusinessException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.AdminUserService;

@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    public AdminUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponseDTO> getAllUsers() {

        return userRepository.findAllByOrderByIdDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id));

        return mapToResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponseDTO updateUserStatus(Long id, boolean enabled) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id));

        // Prevent SUPER_ADMIN from disabling their own account
        if (!enabled
                && user.getRole() != null
                && user.getRole().getName() == RoleType.SUPER_ADMIN) {

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String currentEmail = authentication.getName();

            if (user.getEmail().equalsIgnoreCase(currentEmail)) {
                throw new BusinessException(
                        "You cannot disable your own SUPER_ADMIN account");
            }

            // Prevent disabling the last active SUPER_ADMIN
            long activeSuperAdmins =
                    userRepository.countByRole_NameAndEnabledTrue(
                            RoleType.SUPER_ADMIN);

            if (activeSuperAdmins <= 1) {
                throw new BusinessException(
                        "Cannot disable the last active SUPER_ADMIN");
            }
        }

        user.setEnabled(enabled);

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    private AdminUserResponseDTO mapToResponse(User user) {

        AdminUserResponseDTO response =
                new AdminUserResponseDTO();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setEnabled(user.getEnabled());

        if (user.getRole() != null) {
            response.setRole(
                    user.getRole().getName().name()
            );
        }

        Organization organization =
                user.getOrganization();

        if (organization != null) {
            response.setOrganizationId(
                    organization.getId()
            );

            response.setOrganizationName(
                    organization.getName()
            );
        }

        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}