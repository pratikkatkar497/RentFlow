package com.RentFlow.service;

import java.util.List;

import com.RentFlow.entity.Role;
import com.RentFlow.enums.RoleType;

public interface RoleService {

    List<Role> getAllRoles();

    Role getRoleByName(RoleType roleType);

}
