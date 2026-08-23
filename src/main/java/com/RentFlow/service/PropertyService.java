package com.RentFlow.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.RentFlow.dto.request.PropertyFilterRequestDTO;
import com.RentFlow.dto.request.PropertyRequestDTO;
import com.RentFlow.dto.response.PropertyResponseDTO;

public interface PropertyService {

    // =========================================================
    // Create Property
    // =========================================================

    PropertyResponseDTO createProperty(
            PropertyRequestDTO request);

    // =========================================================
    // Get Property By ID
    // =========================================================

    PropertyResponseDTO getPropertyById(
            Long id);

    // =========================================================
    // Get My Properties - Pagination
    // =========================================================

    Page<PropertyResponseDTO> getMyProperties(
            Pageable pageable);

    // =========================================================
    // Search + Filter + Pagination
    // =========================================================

    Page<PropertyResponseDTO> filterProperties(
            PropertyFilterRequestDTO request,
            Pageable pageable);

    // =========================================================
    // Update Property
    // =========================================================

    PropertyResponseDTO updateProperty(
            Long id,
            PropertyRequestDTO request);

    // =========================================================
    // Delete Property
    // =========================================================

    void deleteProperty(
            Long id);
}