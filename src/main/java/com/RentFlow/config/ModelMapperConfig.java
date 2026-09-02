package com.RentFlow.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.RentFlow.dto.response.PropertyResponseDTO;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(
                        MatchingStrategies.STRICT);

        /*
         * Property → PropertyResponseDTO
         *
         * ownerId and ownerName are calculated manually
         * in the service layer because they come from
         * Property → User.
         */
        modelMapper.createTypeMap(
                com.RentFlow.entity.Property.class,
                PropertyResponseDTO.class
        ).addMappings(mapper -> {

            mapper.skip(
                    PropertyResponseDTO::setOwnerId);

            mapper.skip(
                    PropertyResponseDTO::setOwnerName);
        });

        return modelMapper;
    }
}