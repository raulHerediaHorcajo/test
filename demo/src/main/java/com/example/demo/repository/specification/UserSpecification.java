package com.example.demo.repository.specification;

import com.example.demo.model.User;
import com.example.demo.repository.criteria.UserCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserSpecification implements Specification<User> {

    private final transient UserCriteria filters;

    public UserSpecification(UserCriteria filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        // Agregar condiciones a las consultas dinámicas
        if (filters.getName() != null) {
            String cleanedFilter = filters.getName().toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + cleanedFilter + "%"));
            query.orderBy(criteriaBuilder.asc(criteriaBuilder.length(root.get("name"))));
        }

        if (filters.getEmail() != null) {
            String cleanedFilter = filters.getEmail().toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + cleanedFilter + "%"));
            query.orderBy(criteriaBuilder.asc(criteriaBuilder.length(root.get("email"))));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSpecification that)) return false;
        return Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters);
    }
}
