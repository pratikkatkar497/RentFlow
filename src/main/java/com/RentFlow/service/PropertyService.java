package com.RentFlow.service;

import java.util.List;

import com.RentFlow.dto.request.PropertyRequestDTO;
import com.RentFlow.dto.response.PropertyResponseDTO;

public interface PropertyService {

    PropertyResponseDTO createProperty(PropertyRequestDTO request);

    PropertyResponseDTO getPropertyById(Long id);

    List<PropertyResponseDTO> getMyProperties();

    PropertyResponseDTO updateProperty(Long id,
                                       PropertyRequestDTO request);

    void deleteProperty(Long id);

}