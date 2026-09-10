package com.RentFlow.service.Impl;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.constant.SubscriptionConstants;
import com.RentFlow.dto.request.LoginRequestDTO;
import com.RentFlow.dto.request.RegisterRequestDTO;
import com.RentFlow.dto.response.LoginResponseDTO;
import com.RentFlow.dto.response.UserResponseDTO;
import com.RentFlow.entity.Organization;
import com.RentFlow.entity.Role;
import com.RentFlow.entity.Subscription;
import com.RentFlow.entity.SubscriptionPlan;
import com.RentFlow.entity.User;
import com.RentFlow.enums.RoleType;
import com.RentFlow.enums.SubscriptionStatus;
import com.RentFlow.exception.EmailAlreadyExistsException;
import com.RentFlow.exception.PhoneAlreadyExistsException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.OrganizationRepository;
import com.RentFlow.repository.RoleRepository;
import com.RentFlow.repository.SubscriptionPlanRepository;
import com.RentFlow.repository.SubscriptionRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.security.jwt.JwtUtil;
import com.RentFlow.service.SubscriptionLimitService;
import com.RentFlow.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final SubscriptionLimitService subscriptionLimitService;
    private final UserDetailsService userDetailsService;
    private final OrganizationRepository organizationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder,
            SubscriptionLimitService subscriptionLimitService,
            ModelMapper modelMapper,
            AuthenticationManager authenticationManager,
            OrganizationRepository organizationRepository,
            SubscriptionRepository subscriptionRepository,
            SubscriptionPlanRepository subscriptionPlanRepository,
            UserDetailsService userDetailsService,
            JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.subscriptionLimitService = subscriptionLimitService;
        this.organizationRepository = organizationRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    @Override
    public UserResponseDTO register(RegisterRequestDTO request) {

        // 1. Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists");
        }

        // 2. Check if phone already exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new PhoneAlreadyExistsException(
                    "Phone number already exists");
        }

        // 3. Get OWNER role
        Role role = roleRepository.findByName(RoleType.OWNER)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found"));

        // 4. Create Organization
        Organization organization = new Organization();

        organization.setName(
                request.getFirstName()
                        + " "
                        + request.getLastName()
                        + "'s Organization"
        );

        organization.setSlug(
                request.getFirstName().toLowerCase()
                        + "-"
                        + request.getLastName().toLowerCase()
                        + "-org"
        );

        organization.setActive(true);

        Organization savedOrganization =
                organizationRepository.save(organization);

        // 5. Find FREE subscription plan
        SubscriptionPlan freePlan =
                subscriptionPlanRepository
                        .findByName(
                                SubscriptionConstants.FREE_PLAN)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "FREE subscription plan not found"));

        // 6. Create FREE subscription
        Subscription subscription = new Subscription();

        LocalDateTime subscriptionStartDate = LocalDateTime.now();

        subscription.setOrganization(savedOrganization);
        subscription.setPlan(freePlan);
        subscription.setPrice(freePlan.getPrice());
        subscription.setStartDate(subscriptionStartDate);
        subscription.setEndDate(subscriptionStartDate.plusYears(100));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setAutoRenew(false);
        subscription.setRenewalDate(null);

        subscriptionRepository.save(subscription);

        // 7. Create OWNER user
        User user = new User();
        

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // 8. Encrypt password
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword())
        );

        // 9. Assign OWNER role
        user.setRole(role);

        // 10. Assign Organization
        user.setOrganization(savedOrganization);

        // 11. Save user
        User savedUser =
                userRepository.save(user);

        // 12. Map User to response DTO
        UserResponseDTO response =
                modelMapper.map(
                        savedUser,
                        UserResponseDTO.class
                );

        // 13. Set role manually
        response.setRole(
                savedUser.getRole()
                        .getName()
                        .name()
        );

        return response;
    }

    @Override
    public LoginResponseDTO login(
            LoginRequestDTO request) {

        // Authenticate email & password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Fetch user from database
        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found"));

        // Load UserDetails
        UserDetails userDetails =
                userDetailsService
                        .loadUserByUsername(
                                request.getEmail());

        // Generate JWT Token
        String token =
                jwtUtil.generateToken(userDetails);

        // Build response
        LoginResponseDTO response =
                new LoginResponseDTO();

        response.setMessage("Login Successful");
        response.setEmail(user.getEmail());
        response.setRole(
                user.getRole()
                        .getName()
                        .name()
        );
        response.setToken(token);

        return response;
    }
    
    
}