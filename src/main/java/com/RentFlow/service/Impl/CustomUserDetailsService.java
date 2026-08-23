package com.RentFlow.service.Impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.RentFlow.entity.Role;
import com.RentFlow.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        com.RentFlow.entity.User user =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: "
                                + email));

        Role role = user.getRole();

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())

                // IMPORTANT
                .authorities(
                        "ROLE_" + role.getName().name()
                )

                .disabled(!user.getEnabled())
                .build();
    }
}