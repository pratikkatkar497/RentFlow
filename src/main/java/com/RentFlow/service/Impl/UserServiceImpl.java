package com.RentFlow.service.Impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.LoginRequestDTO;
import com.RentFlow.dto.request.RegisterRequestDTO;
import com.RentFlow.dto.response.LoginResponseDTO;
import com.RentFlow.dto.response.UserResponseDTO;
import com.RentFlow.entity.Role;
import com.RentFlow.entity.User;
import com.RentFlow.enums.RoleType;
import com.RentFlow.exception.EmailAlreadyExistsException;
import com.RentFlow.exception.PhoneAlreadyExistsException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.RoleRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.security.jwt.JwtUtil;
import com.RentFlow.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder,
            ModelMapper modelMapper,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    @Override
    public UserResponseDTO register(RegisterRequestDTO request) {

    	if (userRepository.existsByEmail(request.getEmail())) {
    	    throw new EmailAlreadyExistsException("Email already exists");
    	}

    	if (userRepository.existsByPhone(request.getPhone())) {
    	    throw new PhoneAlreadyExistsException("Phone number already exists");
    	}

        Role role = roleRepository.findByName(RoleType.TENANT)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found"));

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(role);

        User savedUser = userRepository.save(user);

        UserResponseDTO response =
                modelMapper.map(savedUser, UserResponseDTO.class);

        response.setRole(savedUser.getRole().getName().name());

        return response;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        // Authenticate email & password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Fetch user from database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // Load UserDetails
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());

        // Generate JWT Token
        String token = jwtUtil.generateToken(userDetails);

        // Build response
        LoginResponseDTO response = new LoginResponseDTO();

        response.setMessage("Login Successful");
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName().name());
        response.setToken(token);

        return response;
    }
}