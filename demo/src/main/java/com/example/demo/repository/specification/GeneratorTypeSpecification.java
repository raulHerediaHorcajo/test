package com.example.demo.repository.specification;

import com.example.demo.model.GeneratorType;
import com.example.demo.repository.criteria.GeneratorTypeCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GeneratorTypeSpecification implements Specification<GeneratorType> {

    private final transient GeneratorTypeCriteria filters;

    public GeneratorTypeSpecification(GeneratorTypeCriteria filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<GeneratorType> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

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
        if (!(o instanceof GeneratorTypeSpecification that)) return false;
        return Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters);
    }
}
