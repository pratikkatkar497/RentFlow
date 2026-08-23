package com.RentFlow.service;

import com.RentFlow.dto.request.ChangePasswordRequestDTO;

public interface PasswordService {

    void changePassword(ChangePasswordRequestDTO request);
}