package com.RentFlow.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.request.LoginRequestDTO;
import com.RentFlow.dto.request.RegisterRequestDTO;
import com.RentFlow.dto.response.LoginResponseDTO;
import com.RentFlow.dto.response.UserResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.UserService;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

	@Autowired
    private  UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO request){

        UserResponseDTO response =
                userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        AppConstants.CREATED,
                        response));

    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        return ResponseEntity.ok(userService.login(request));
    }

}