package com.RentFlow.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.request.OrganizationRequestDTO;
import com.RentFlow.dto.response.OrganizationResponseDTO;
import com.RentFlow.entity.Organization;
import com.RentFlow.entity.User;
import com.RentFlow.enums.RoleType;
import com.RentFlow.exception.BusinessException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.OrganizationRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.OrganizationService;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    private final UserRepository userRepository;

    public OrganizationServiceImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository) {

        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // CREATE ORGANIZATION
    // =========================================================

    @Override
    @Transactional
    public OrganizationResponseDTO createOrganization(
            OrganizationRequestDTO request) {

        if (organizationRepository.existsBySlug(request.getSlug())) {

            throw new BusinessException(
                    "Organization slug already exists: "
                            + request.getSlug()
            );
        }

        Organization organization =
                new Organization();

        organization.setName(request.getName());
        organization.setDescription(request.getDescription());
        organization.setSlug(request.getSlug());
        organization.setActive(true);

        Organization savedOrganization =
                organizationRepository.save(organization);

        return mapToResponse(savedOrganization);
    }

    // =========================================================
    // GET ORGANIZATION BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponseDTO getOrganizationById(
            Long id) {

        Organization organization =
                organizationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(organization);
    }

    // =========================================================
    // GET ALL ORGANIZATIONS
    // Includes ACTIVE + INACTIVE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponseDTO> getAllOrganizations() {

        return organizationRepository
                .findAllByOrderByIdDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET ACTIVE ORGANIZATIONS ONLY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponseDTO>
            getAllActiveOrganizations() {

        return organizationRepository
                .findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // UPDATE ORGANIZATION
    // =========================================================

    @Override
    @Transactional
    public OrganizationResponseDTO updateOrganization(
            Long id,
            OrganizationRequestDTO request) {

        Organization organization =
                organizationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found with id: "
                                                + id
                                )
                        );

        /*
         * Slug should remain stable after organization creation.
         * We only update name and description.
         */

        organization.setName(request.getName());

        organization.setDescription(
                request.getDescription()
        );

        Organization updatedOrganization =
                organizationRepository.save(organization);

        return mapToResponse(updatedOrganization);
    }

    // =========================================================
    // DEACTIVATE ORGANIZATION
    // =========================================================

    @Override
    @Transactional
    public void deactivateOrganization(Long id) {

        Organization organization =
                organizationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found with id: "
                                                + id
                                )
                        );

        if (Boolean.FALSE.equals(
                organization.getActive())) {

            throw new BusinessException(
                    "Organization is already inactive"
            );
        }

        organization.setActive(false);

        organizationRepository.save(organization);
    }

    // =========================================================
    // ACTIVATE ORGANIZATION
    // =========================================================

    @Override
    @Transactional
    public void activateOrganization(Long id) {

        Organization organization =
                organizationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found with id: "
                                                + id
                                )
                        );

        if (Boolean.TRUE.equals(
                organization.getActive())) {

            throw new BusinessException(
                    "Organization is already active"
            );
        }

        organization.setActive(true);

        organizationRepository.save(organization);
    }

    // =========================================================
    // MAP ENTITY → RESPONSE DTO
    // =========================================================

    private OrganizationResponseDTO mapToResponse(
            Organization organization) {

        OrganizationResponseDTO response =
                new OrganizationResponseDTO();

        // Organization details
        response.setId(organization.getId());
        response.setName(organization.getName());
        response.setDescription(organization.getDescription());
        response.setSlug(organization.getSlug());
        response.setActive(organization.getActive());
        response.setCreatedAt(organization.getCreatedAt());
        response.setUpdatedAt(organization.getUpdatedAt());

        // Primary Owner
        User owner = organization.getOwner();

        if (owner != null) {

            response.setOwnerId(owner.getId());

            response.setOwnerName(
                    owner.getFirstName()
                            + " "
                            + owner.getLastName()
            );

            response.setOwnerEmail(
                    owner.getEmail()
            );
        }

        return response;
    }
    
    
    @Override
    @Transactional
    public void assignOwner(
            Long organizationId,
            Long ownerId) {

        // Find organization
        Organization organization =
                organizationRepository.findById(organizationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found with id: "
                                                + organizationId));

        // Organization must be active
        if (!Boolean.TRUE.equals(
                organization.getActive())) {

            throw new BusinessException(
                    "Cannot assign owner to an inactive organization");
        }

        // Find selected user
        User owner =
                userRepository.findById(ownerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Owner not found with id: "
                                                + ownerId));

        // Must have OWNER role
        if (owner.getRole() == null ||
                owner.getRole().getName() != RoleType.OWNER) {

            throw new BusinessException(
                    "Selected user is not an OWNER");
        }

        // Owner must be enabled
        if (!Boolean.TRUE.equals(
                owner.getEnabled())) {

            throw new BusinessException(
                    "Cannot assign an inactive owner");
        }

        // Owner must belong to this organization
        if (owner.getOrganization() == null ||
                !organization.getId().equals(
                        owner.getOrganization().getId())) {

            throw new BusinessException(
                    "Owner does not belong to this organization");
        }

        // Already the primary owner
        if (organization.getOwner() != null &&
                organization.getOwner().getId()
                        .equals(owner.getId())) {

            throw new BusinessException(
                    "This user is already the primary owner");
        }

        // Assign / change primary owner
        organization.setOwner(owner);

        organizationRepository.save(organization);
    }
}