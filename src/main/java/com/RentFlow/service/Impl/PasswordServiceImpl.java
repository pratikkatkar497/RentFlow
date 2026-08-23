package com.RentFlow.service.Impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.RentFlow.dto.request.ChangePasswordRequestDTO;
import com.RentFlow.entity.User;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.PasswordService;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordServiceImpl(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void changePassword(
            ChangePasswordRequestDTO request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        // Check current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Current password is incorrect");
        }

        // Check new password confirmation
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new RuntimeException(
                    "New password and confirm password do not match");
        }

        // Prevent same password
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "New password must be different from current password");
        }

        // Encode and save new password
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        userRepository.save(user);
    }
}