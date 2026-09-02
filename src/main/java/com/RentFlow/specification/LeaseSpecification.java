package com.RentFlow.specification;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.RentFlow.entity.Lease;
import com.RentFlow.entity.Property;
import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.LeaseStatus;

public class LeaseSpecification {

    private LeaseSpecification() {
    }

    public static Specification<Lease> filterLeases(
            User owner,
            String search,
            Long propertyId,
            Long tenantId,
            LeaseStatus status,
            BigDecimal minRent,
            BigDecimal maxRent,
            LocalDate startDateFrom,
            LocalDate startDateTo,
            LocalDate endDateFrom,
            LocalDate endDateTo) {

        return (root, query, criteriaBuilder) -> {

            Predicate predicate =
                    criteriaBuilder.conjunction();

            // =====================================================
            // OWNER SECURITY FILTER
            // =====================================================

            Join<Lease, Property> property =
                    root.join(
                            "property",
                            JoinType.INNER);

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(
                            property.get("owner"),
                            owner));

            // =====================================================
            // TENANT JOIN
            // =====================================================

            Join<Lease, Tenant> tenant =
                    root.join(
                            "tenant",
                            JoinType.INNER);

            // =====================================================
            // SEARCH
            //
            // Property name
            // Tenant first name
            // Tenant last name
            // =====================================================

            if (search != null &&
                    !search.trim().isEmpty()) {

                String searchPattern =
                        "%" +
                        search.trim().toLowerCase() +
                        "%";

                Predicate propertyName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        property.get(
                                                "propertyName")),
                                searchPattern);

                Predicate tenantFirstName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        tenant.get(
                                                "firstName")),
                                searchPattern);

                Predicate tenantLastName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        tenant.get(
                                                "lastName")),
                                searchPattern);

                Predicate searchPredicate =
                        criteriaBuilder.or(
                                propertyName,
                                tenantFirstName,
                                tenantLastName);

                predicate =
                        criteriaBuilder.and(
                                predicate,
                                searchPredicate);
            }

            // =====================================================
            // PROPERTY ID
            // =====================================================

            if (propertyId != null) {

                predicate =
                        criteriaBuilder.and(
                                predicate,
                                criteriaBuilder.equal(
                                        property.get("id"),
                                        propertyId));
            }

            // =====================================================
            // TENANT ID
            // =====================================================

            if (tenantId != null) {

                predicate =
                        criteriaBuilder.and(
                                predicate,
                                criteriaBuilder.equal(
                                        tenant.get("id"),
                                        tenantId));
            }

            // =====================================================
            // STATUS
            // =====================================================

            if (status != null) {

                predicate =
                        criteriaBuilder.and(
                                predicate,
                                criteriaBuilder.equal(
                                        root.get("status"),
                                        status));
            }

            // =====================================================
            // MINIMUM RENT
            // =====================================================

            if (minRent != null) {

                predicate =
                        criteriaBuilder.and(
                                predicate,
                                criteriaBuilder
                                        .greaterThanOrEqualTo(
                                                root.get(
                                                        "monthlyRent"),
                                                minRent));
            }

            // =====================================================
            // MAXIMUM RENT
            // =====================================================

            if (maxRent != null) {

                predicate =
                        criteriaBuilder.and(
                                predicate,
                                criteriaBuilder
                                        .lessThanOrEqualTo(
                                                root.get(
                                                        "monthlyRent"),
                                                maxRent));
            }

            // =====================================================
            // START DATE FROM
            // =====================================================

            if (startDateFrom != null) {

                predicate =
                        criteriaBuilder.and(
                                predicate,
                                criteriaBuilder
                                        .greaterThanOrEqualTo(
                                                root.get(
                                                        "startDate"),
                                                startDateFrom));
            }

            // =====================================================
            // START DATE TO
            // =====================================================

            if (startDateTo != null) {

                predicate =
                        criteriaBuilder.and(
                                predicate,
                                criteriaBuilder
                                        .lessThanOrEqualTo(
                                                root.get(
                                                        "startDate"),
                                                startDateTo));
            }

            // =====================================================
            // END DATE FROM
            // =====================================================

            if (endDateFrom != null) {

                predicate =
                        criteriaBuilder.and(
                                predicate,
                                criteriaBuilder
                                        .greaterThanOrEqualTo(
                                                root.get(
                                                        "endDate"),
                                                endDateFrom));
            }

            // =====================================================
            // END DATE TO
            // =====================================================

            if (endDateTo != null) {

                predicate =
                        criteriaBuilder.and(
                                predicate,
                                criteriaBuilder
                                        .lessThanOrEqualTo(
                                                root.get(
                                                        "endDate"),
                                                endDateTo));
            }

            return predicate;
        };
    }
}