package com.smartbank.customer.specification;

import com.smartbank.customer.dto.CustomerSearchCriteria;
import com.smartbank.customer.entity.Customer;
import com.smartbank.customer.entity.CustomerStatus;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification {

    private CustomerSpecification() {
        // Utility class
    }

    public static Specification<Customer> byCriteria(
            CustomerSearchCriteria criteria
    ) {

        return Specification.allOf(
                hasStatus(criteria.getStatus()),
                hasName(criteria.getName()),
                hasCifId(criteria.getCifId()),
                hasEmail(criteria.getEmail())
        );
    }

    public static Specification<Customer> hasStatus(CustomerStatus status){

        return (root, query, criteriaBuilder) ->
                        status == null
                        ? null
                                : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Customer> hasName(String name) {

        return (root, query, criteriaBuilder) ->
                name == null || name.isBlank()
                        ? null
                        : criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("firstName")),
                                "%" + name.toLowerCase() + "%"
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("lastName")),
                                "%" + name.toLowerCase() + "%"
                        )
                );
    }

    public static Specification<Customer> hasCifId(String cifId) {

        return (root,query,criteriaBuilder) ->
                cifId == null || cifId.isBlank()
                ? null
                : criteriaBuilder.equal(root.get("cifId"), cifId);
    }

    public static Specification<Customer> hasEmail(String email) {

        return (root, query, criteriaBuilder) ->
                email == null || email.isBlank()
                        ? null
                        : criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("email")),
                        email.toLowerCase()
                );
    }
}
