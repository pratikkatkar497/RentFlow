package com.RentFlow.service;

import java.util.List;

import com.RentFlow.dto.response.AdminUserResponseDTO;

public interface AdminUserService {

    List<AdminUserResponseDTO> getAllUsers();

    AdminUserResponseDTO getUserById(Long id);

    AdminUserResponseDTO updateUserStatus(Long id, boolean enabled);
}