package com.RentFlow.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.OrganizationUserRequestDTO;
import com.RentFlow.dto.response.UserResponseDTO;
import com.RentFlow.entity.Role;
import com.RentFlow.entity.User;
import com.RentFlow.enums.RoleType;
import com.RentFlow.exception.BadRequestException;
import com.RentFlow.exception.DuplicateResourceException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.RoleRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.OrganizationUserService;
import com.RentFlow.service.SubscriptionLimitService;

@Service
public class OrganizationUserServiceImpl
        implements OrganizationUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionLimitService subscriptionLimitService;

    public OrganizationUserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            SubscriptionLimitService subscriptionLimitService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionLimitService = subscriptionLimitService;
    }

    @Transactional
    @Override
    public UserResponseDTO addUser(
            OrganizationUserRequestDTO request) {

        // Get currently logged-in user
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User currentUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Current user not found"));

        // Only OWNER can add organization users
        if (currentUser.getRole().getName()
                != RoleType.OWNER) {

            throw new BadRequestException(
                    "Only OWNER can add organization users");
        }

        // Get organization
        if (currentUser.getOrganization() == null) {

            throw new ResourceNotFoundException(
                    "Organization not found for current user");
        }

        Long organizationId =
                currentUser.getOrganization().getId();

        // Check subscription
        subscriptionLimitService
                .checkSubscriptionActive(
                        organizationId);

        // Check user limit
        subscriptionLimitService
                .checkUserLimit(
                        organizationId);

        // Check email
        if (userRepository.existsByEmail(
                request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already exists");
        }

        // Check phone
        if (userRepository.existsByPhone(
                request.getPhone())) {

            throw new DuplicateResourceException(
                    "Phone number already exists");
        }

        // Do not allow SUPER_ADMIN creation
        if (request.getRole() != RoleType.MANAGER) {

            throw new BadRequestException(
                    "Only MANAGER users can be created through this API");
        }
        // Find requested role
        Role role =
                roleRepository.findByName(
                        request.getRole())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Role not found"));

        // Create User
        User user = new User();

        user.setFirstName(
                request.getFirstName());

        user.setLastName(
                request.getLastName());

        user.setEmail(
                request.getEmail());

        user.setPhone(
                request.getPhone());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));

        user.setRole(role);

        // IMPORTANT:
        // User automatically belongs to
        // current user's organization
        user.setOrganization(
                currentUser.getOrganization());

        user.setEnabled(true);

        User savedUser =
                userRepository.save(user);

        // Create response
        UserResponseDTO response =
                new UserResponseDTO();

        response.setId(savedUser.getId());
        response.setFirstName(
                savedUser.getFirstName());
        response.setLastName(
                savedUser.getLastName());
        response.setEmail(
                savedUser.getEmail());
        response.setPhone(
                savedUser.getPhone());

        response.setRole(
                savedUser.getRole()
                        .getName()
                        .name());

        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getOrganizationUsers() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User currentUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Current user not found"));

        if (currentUser.getOrganization() == null) {
            throw new ResourceNotFoundException(
                    "Organization not found for current user");
        }

        Long organizationId =
                currentUser.getOrganization().getId();

        List<User> users =
                userRepository.findByOrganizationId(
                        organizationId);

        return users.stream()
                .map(user -> {

                    UserResponseDTO response =
                            new UserResponseDTO();

                    response.setId(user.getId());
                    response.setFirstName(
                            user.getFirstName());
                    response.setLastName(
                            user.getLastName());
                    response.setEmail(
                            user.getEmail());
                    response.setPhone(
                            user.getPhone());

                    response.setRole(
                            user.getRole()
                                    .getName()
                                    .name());

                    return response;

                })
                .collect(Collectors.toList());
    }
}