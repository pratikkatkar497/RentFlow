package com.RentFlow.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.dto.request.UpdateProfileRequestDTO;
import com.RentFlow.dto.response.ProfileResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponseDTO>>
    getMyProfile() {

        ProfileResponseDTO response =
                profileService.getMyProfile();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile fetched successfully",
                        response
                )
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProfileResponseDTO>>
    updateMyProfile(
            @Valid @RequestBody UpdateProfileRequestDTO request) {

        ProfileResponseDTO response =
                profileService.updateMyProfile(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile updated successfully",
                        response
                )
        );
    }
}