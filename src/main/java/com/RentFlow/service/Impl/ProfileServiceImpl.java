package com.RentFlow.service.Impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.UpdateProfileRequestDTO;
import com.RentFlow.dto.response.ProfileResponseDTO;
import com.RentFlow.entity.User;
import com.RentFlow.exception.PhoneAlreadyExistsException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    public ProfileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDTO getMyProfile() {

        User user = getCurrentUser();

        return mapToResponse(user);
    }

    @Override
    @Transactional
    public ProfileResponseDTO updateMyProfile(
            UpdateProfileRequestDTO request) {

        User user = getCurrentUser();

        // Check phone only if it is being changed
        if (!user.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {

            throw new PhoneAlreadyExistsException(
                    "Phone number already exists");
        }

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhone(request.getPhone().trim());

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {

            throw new ResourceNotFoundException(
                    "Authenticated user not found");
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }

    private ProfileResponseDTO mapToResponse(User user) {

        ProfileResponseDTO response =
                new ProfileResponseDTO();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setEnabled(user.getEnabled());

        if (user.getRole() != null) {
            response.setRole(
                    user.getRole()
                        .getName()
                        .name()
            );
        }

        if (user.getOrganization() != null) {
            response.setOrganizationId(
                    user.getOrganization().getId()
            );

            response.setOrganizationName(
                    user.getOrganization().getName()
            );
        }

        return response;
    }
}