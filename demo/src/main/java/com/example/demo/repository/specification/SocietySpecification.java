package com.example.demo.repository.specification;

import com.example.demo.model.Society;
import com.example.demo.repository.criteria.SocietyCriteria;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SocietySpecification implements Specification<Society> {

    private final transient SocietyCriteria filters;

    public SocietySpecification(SocietyCriteria filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<Society> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        // Agregar condiciones a las consultas dinámicas
        if (filters.getCifDni() != null) {
            String cleanedFilter = filters.getCifDni().toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("cifDni")), "%" + cleanedFilter + "%"));
            query.orderBy(criteriaBuilder.asc(criteriaBuilder.length(root.get("cifDni"))));
        }

        if (filters.getName() != null) {
            String cleanedFilter = filters.getName().toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + cleanedFilter + "%"));
            query.orderBy(criteriaBuilder.asc(criteriaBuilder.length(root.get("name"))));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocietySpecification that)) return false;
        return Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters);
    }
}
