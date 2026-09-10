package com.RentFlow.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.dto.request.OrganizationUserRequestDTO;
import com.RentFlow.dto.response.UserResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.OrganizationUserService;

@RestController
@RequestMapping("/api/organization/users")
public class OrganizationUserController {
	private final OrganizationUserService organizationUserService;

	public OrganizationUserController(OrganizationUserService organizationUserService) {
		this.organizationUserService = organizationUserService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<UserResponseDTO>> addUser(
			@Valid @RequestBody OrganizationUserRequestDTO request) {
		UserResponseDTO response = organizationUserService.addUser(request);
		ApiResponse<UserResponseDTO> apiResponse = new ApiResponse<>(true, "Organization user created successfully",
				response);
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getOrganizationUsers() {

	    List<UserResponseDTO> users =
	            organizationUserService.getOrganizationUsers();

	    ApiResponse<List<UserResponseDTO>> apiResponse =
	            new ApiResponse<>(
	                    true,
	                    "Organization users retrieved successfully",
	                    users
	            );

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(apiResponse);
	}
}