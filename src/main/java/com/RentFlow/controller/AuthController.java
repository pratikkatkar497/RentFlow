package com.RentFlow.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.request.ChangePasswordRequestDTO;
import com.RentFlow.dto.request.LoginRequestDTO;
import com.RentFlow.dto.request.RegisterRequestDTO;
import com.RentFlow.dto.response.LoginResponseDTO;
import com.RentFlow.dto.response.UserResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.PasswordService;
import com.RentFlow.service.UserService;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

	@Autowired
    private  UserService userService;
	private final PasswordService passwordService;
	
	public AuthController(
	        PasswordService passwordService) {
	    this.passwordService = passwordService;
	}

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
    
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request) {

        passwordService.changePassword(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Password changed successfully.",
                        null));
    }

}