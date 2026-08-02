package com.RentFlow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RentFlow.entity.Role;
import com.RentFlow.enums.RoleType;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);

    boolean existsByName(RoleType name);

}