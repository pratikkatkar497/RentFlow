package com.RentFlow.service;

import java.util.List;

import com.RentFlow.dto.request.OrganizationUserRequestDTO;
import com.RentFlow.dto.response.UserResponseDTO;

public interface OrganizationUserService {

    UserResponseDTO addUser(
            OrganizationUserRequestDTO request);
    List<UserResponseDTO> getOrganizationUsers();
}