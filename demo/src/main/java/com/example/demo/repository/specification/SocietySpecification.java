package com.example.demo.repository.specification;

import com.example.demo.model.Society;
import com.example.demo.repository.criteria.SocietyCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
            predicates.add(criteriaBuilder.equal(root.get("cifDni"), filters.getCifDni()));
        }

        if (filters.getName() != null) {
            predicates.add(criteriaBuilder.equal(root.get("name"), filters.getName()));
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
