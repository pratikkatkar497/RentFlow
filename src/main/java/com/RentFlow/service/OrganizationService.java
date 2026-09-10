package com.RentFlow.service;

import java.util.List;

import com.RentFlow.dto.request.OrganizationRequestDTO;
import com.RentFlow.dto.response.OrganizationResponseDTO;

public interface OrganizationService {

	void assignOwner(Long organizationId, Long ownerId);
	
    OrganizationResponseDTO createOrganization(
            OrganizationRequestDTO request
    );

    OrganizationResponseDTO getOrganizationById(
            Long id
    );

    List<OrganizationResponseDTO> getAllOrganizations();

    List<OrganizationResponseDTO> getAllActiveOrganizations();

    OrganizationResponseDTO updateOrganization(
            Long id,
            OrganizationRequestDTO request
    );

    void deactivateOrganization(Long id);

    void activateOrganization(Long id);
}