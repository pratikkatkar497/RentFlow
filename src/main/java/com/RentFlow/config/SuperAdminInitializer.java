package com.RentFlow.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.entity.Role;
import com.RentFlow.entity.User;
import com.RentFlow.enums.RoleType;
import com.RentFlow.repository.RoleRepository;
import com.RentFlow.repository.UserRepository;

@Component
public class SuperAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public SuperAdminInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        boolean superAdminExists =
                userRepository.countByRole_Name(RoleType.SUPER_ADMIN) > 0;

        if (superAdminExists) {
            return;
        }

        Role superAdminRole =
                roleRepository.findByName(RoleType.SUPER_ADMIN)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "SUPER_ADMIN role not found"));

        User superAdmin = new User();

        superAdmin.setFirstName("Super");
        superAdmin.setLastName("Admin");
        superAdmin.setEmail("admin@gmail.com");

        superAdmin.setPassword(
                passwordEncoder.encode("Admin@123")
        );

        superAdmin.setPhone("7720822148");
        superAdmin.setEnabled(true);
        superAdmin.setRole(superAdminRole);
        superAdmin.setOrganization(null);

        userRepository.save(superAdmin);

        System.out.println(
                "=========================================="
        );
        System.out.println(
                "RentFlow SUPER_ADMIN created successfully"
        );
        System.out.println(
                "Email    : admin@rentflow.com"
        );
        System.out.println(
                "Password : Admin@123"
        );
        System.out.println(
                "=========================================="
        );
    }
}