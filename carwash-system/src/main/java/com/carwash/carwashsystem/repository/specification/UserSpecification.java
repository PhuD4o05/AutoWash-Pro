package com.carwash.carwashsystem.repository.specification;

import com.carwash.carwashsystem.entity.Account;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.enums.Role;
import com.carwash.carwashsystem.enums.Status;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<Account> filter(

            String keyword,

            String role,

            String status



    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Join sang Customer
            Join<Account, Customer> customer =
                    root.join("customer", JoinType.LEFT);

            // SEARCH
            if (keyword != null && !keyword.isBlank()) {

                String key = "%" + keyword.toLowerCase().trim() + "%";

                predicates.add(

                        cb.or(

                                cb.like(
                                        cb.lower(root.get("email")),
                                        key
                                ),

                                cb.like(
                                        cb.lower(customer.get("fullName")),
                                        key
                                ),

                                cb.like(
                                        customer.get("phoneNumber"),
                                        "%" + keyword.trim() + "%"
                                )

                        )

                );

            }

            // FILTER ROLE
            if (role != null && !role.isBlank()) {

                predicates.add(

                        cb.equal(
                                root.get("role"),
                                Role.valueOf(role.toUpperCase())
                        )

                );

            }

            // FILTER STATUS
            if (status != null && !status.isBlank()) {

                predicates.add(

                        cb.equal(
                                root.get("status"),
                                Status.valueOf(status.toUpperCase())
                        )

                );

            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };

    }


}