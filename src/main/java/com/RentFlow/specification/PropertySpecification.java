package com.RentFlow.specification;

import java.math.BigDecimal;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.RentFlow.entity.Property;
import com.RentFlow.entity.User;
import com.RentFlow.enums.PropertyStatus;
import com.RentFlow.enums.PropertyType;

public class PropertySpecification {

    private PropertySpecification() {
    }

    public static Specification<Property> filterProperties(
            User owner,
            String search,
            String city,
            String state,
            PropertyType propertyType,
            PropertyStatus status,
            BigDecimal minRent,
            BigDecimal maxRent) {

        return (root, query, criteriaBuilder) -> {

            Predicate predicate =
                    criteriaBuilder.conjunction();

            // =====================================================
            // Owner
            // =====================================================

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(
                            root.get("owner"),
                            owner));

            // =====================================================
            // Search
            // propertyName OR description OR city
            // =====================================================

            if (search != null &&
                    !search.trim().isEmpty()) {

                String searchPattern =
                        "%" + search.trim().toLowerCase() + "%";

                Predicate propertyName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("propertyName")),
                                searchPattern);

                Predicate description =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("description")),
                                searchPattern);

                Predicate citySearch =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("city")),
                                searchPattern);

                Predicate searchPredicate =
                        criteriaBuilder.or(
                                propertyName,
                                description,
                                citySearch);

                predicate = criteriaBuilder.and(
                        predicate,
                        searchPredicate);
            }

            // =====================================================
            // City
            // =====================================================

            if (city != null &&
                    !city.trim().isEmpty()) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(
                                        root.get("city")),
                                city.trim().toLowerCase()));
            }

            // =====================================================
            // State
            // =====================================================

            if (state != null &&
                    !state.trim().isEmpty()) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(
                                        root.get("state")),
                                state.trim().toLowerCase()));
            }

            // =====================================================
            // Property Type
            // =====================================================

            if (propertyType != null) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("propertyType"),
                                propertyType));
            }

            // =====================================================
            // Property Status
            // =====================================================

            if (status != null) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("status"),
                                status));
            }

            // =====================================================
            // Minimum Rent
            // =====================================================

            if (minRent != null) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("monthlyRent"),
                                minRent));
            }

            // =====================================================
            // Maximum Rent
            // =====================================================

            if (maxRent != null) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("monthlyRent"),
                                maxRent));
            }

            return predicate;
        };
    }
}