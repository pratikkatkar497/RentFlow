package com.RentFlow.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RentFlow.entity.Role;
import com.RentFlow.enums.RoleType;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.RoleRepository;
import com.RentFlow.service.RoleService;


@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	@Override
	public Role getRoleByName(RoleType roleType) {

		return roleRepository.findByName(roleType).orElseThrow(() -> new ResourceNotFoundException("Role not found"));

	}
}
