package com.RentFlow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.entity.Role;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.RoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RoleController {

	@Autowired
	private RoleService roleService;

	@GetMapping("/api/roles")
	public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {

		List<Role> roles = roleService.getAllRoles();

		ApiResponse<List<Role>> response = new ApiResponse<>(true, "Roles fetched successfully", roles);

		return ResponseEntity.ok(response);
	}

}
