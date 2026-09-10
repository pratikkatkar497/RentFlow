package com.RentFlow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.RentFlow.entity.User;
import com.RentFlow.enums.RoleType;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    long countByOrganizationId(Long organizationId);

    boolean existsByPhone(String phone);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    List<User> findByOrganizationId(Long organizationId);

    long countByRole_Name(RoleType roleName);

    List<User> findAllByOrderByIdDesc();

    long countByRole_NameAndEnabledTrue(RoleType roleName);

    List<User> findByRole_NameOrderByIdDesc(RoleType roleName);

    Optional<User> findFirstByOrganizationIdAndRole_Name(
            Long organizationId,
            RoleType roleName
    );
}