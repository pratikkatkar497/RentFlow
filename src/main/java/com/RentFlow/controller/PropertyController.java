package com.RentFlow.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.request.PropertyFilterRequestDTO;
import com.RentFlow.dto.request.PropertyRequestDTO;
import com.RentFlow.dto.response.PropertyResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.PropertyService;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(
            PropertyService propertyService) {

        this.propertyService =
                propertyService;
    }

    // =========================================================
    // Create Property
    // =========================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<PropertyResponseDTO>>
            createProperty(
                    @Valid
                    @RequestBody
                    PropertyRequestDTO request) {

        PropertyResponseDTO response =
                propertyService.createProperty(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                AppConstants.CREATED,
                                response));
    }

    // =========================================================
    // Get My Properties - PAGINATION
    // =========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<Page<PropertyResponseDTO>>>
            getMyProperties(

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        Page<PropertyResponseDTO> response =
                propertyService.getMyProperties(
                        pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }
    // =========================================================
    // Get Property By ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<PropertyResponseDTO>>
            getPropertyById(
                    @PathVariable Long id) {

        PropertyResponseDTO response =
                propertyService.getPropertyById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // =========================================================
    // Update Property
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<PropertyResponseDTO>>
            updateProperty(
                    @PathVariable Long id,

                    @Valid
                    @RequestBody
                    PropertyRequestDTO request) {

        PropertyResponseDTO response =
                propertyService.updateProperty(
                        id,
                        request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.UPDATED,
                        response));
    }

    // =========================================================
    // Delete Property
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<
            ApiResponse<Object>>
            deleteProperty(
                    @PathVariable Long id) {

        propertyService.deleteProperty(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.DELETED,
                        null));
    }
    
 // =========================================================
 // Search + Filter + Pagination + Sorting
 // =========================================================

 @GetMapping("/filter")
 public ResponseEntity<
         ApiResponse<Page<PropertyResponseDTO>>>
         filterProperties(

         @ModelAttribute
         PropertyFilterRequestDTO request,

         @PageableDefault(
                 page = 0,
                 size = 10,
                 sort = "id",
                 direction = Sort.Direction.DESC
         )
         Pageable pageable) {

     Page<PropertyResponseDTO> response =
             propertyService.filterProperties(
                     request,
                     pageable);

     return ResponseEntity.ok(
             new ApiResponse<>(
                     true,
                     AppConstants.SUCCESS,
                     response));
 }
}