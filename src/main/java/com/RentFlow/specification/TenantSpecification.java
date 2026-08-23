package com.RentFlow.specification;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.RentFlow.entity.Tenant;
import com.RentFlow.entity.User;
import com.RentFlow.enums.TenantStatus;

public class TenantSpecification {

    private TenantSpecification() {
    }

    public static Specification<Tenant> filterTenants(
            User owner,
            String search,
            TenantStatus status,
            String occupation,
            String companyName) {

        return (root, query, criteriaBuilder) -> {

            Predicate predicate =
                    criteriaBuilder.conjunction();

            // =====================================================
            // Owner isolation
            // =====================================================

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(
                            root.get("property")
                                 .get("owner"),
                            owner));

            // =====================================================
            // Search
            //
            // Searches:
            // firstName
            // lastName
            // email
            // phone
            // =====================================================

            if (search != null &&
                    !search.trim().isEmpty()) {

                String searchPattern =
                        "%" +
                        search.trim().toLowerCase() +
                        "%";

                Predicate firstName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("firstName")),
                                searchPattern);

                Predicate lastName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("lastName")),
                                searchPattern);

                Predicate email =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("email")),
                                searchPattern);

                Predicate phone =
                        criteriaBuilder.like(
                                root.get("phone"),
                                searchPattern);

                Predicate searchPredicate =
                        criteriaBuilder.or(
                                firstName,
                                lastName,
                                email,
                                phone);

                predicate = criteriaBuilder.and(
                        predicate,
                        searchPredicate);
            }

            // =====================================================
            // Status
            // =====================================================

            if (status != null) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("status"),
                                status));
            }

            // =====================================================
            // Occupation
            // =====================================================

            if (occupation != null &&
                    !occupation.trim().isEmpty()) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("occupation")),
                                "%" +
                                occupation.trim().toLowerCase() +
                                "%"));
            }

            // =====================================================
            // Company
            // =====================================================

            if (companyName != null &&
                    !companyName.trim().isEmpty()) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("companyName")),
                                "%" +
                                companyName.trim().toLowerCase() +
                                "%"));
            }

            return predicate;
        };
    }
}