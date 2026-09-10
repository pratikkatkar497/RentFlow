package com.RentFlow.service;

import com.RentFlow.dto.request.UpdateProfileRequestDTO;
import com.RentFlow.dto.response.ProfileResponseDTO;

public interface ProfileService {

    ProfileResponseDTO getMyProfile();

    ProfileResponseDTO updateMyProfile(
            UpdateProfileRequestDTO request);
}