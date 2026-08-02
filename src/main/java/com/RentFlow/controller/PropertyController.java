package com.RentFlow.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.request.PropertyRequestDTO;
import com.RentFlow.dto.response.PropertyResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.PropertyService;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // Create Property
    @PostMapping
    public ResponseEntity<ApiResponse<PropertyResponseDTO>> createProperty(
            @Valid @RequestBody PropertyRequestDTO request) {

        PropertyResponseDTO response = propertyService.createProperty(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        AppConstants.CREATED,
                        response));
    }

    // Get My Properties
    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyResponseDTO>>> getMyProperties() {

        List<PropertyResponseDTO> response = propertyService.getMyProperties();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // Get Property By Id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponseDTO>> getPropertyById(
            @PathVariable Long id) {

        PropertyResponseDTO response =
                propertyService.getPropertyById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // Update Property
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponseDTO>> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRequestDTO request) {

        PropertyResponseDTO response =
                propertyService.updateProperty(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.UPDATED,
                        response));
    }

    // Delete Property
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProperty(
            @PathVariable Long id) {

        propertyService.deleteProperty(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.DELETED,
                        null));
    }
}