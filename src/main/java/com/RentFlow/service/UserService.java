package com.RentFlow.service;

import com.RentFlow.dto.request.LoginRequestDTO;
import com.RentFlow.dto.request.RegisterRequestDTO;
import com.RentFlow.dto.response.LoginResponseDTO;
import com.RentFlow.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO register(RegisterRequestDTO request);
    LoginResponseDTO login(LoginRequestDTO request);
}