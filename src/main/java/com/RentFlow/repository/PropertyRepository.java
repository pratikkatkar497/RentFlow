package com.RentFlow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RentFlow.entity.Property;
import com.RentFlow.entity.User;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwner(User owner);

    Optional<Property> findByIdAndOwner(Long id, User owner);

}