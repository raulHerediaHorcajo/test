package com.example.demo.repository.specification;

import com.example.demo.model.Society;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocietySpecification implements Specification<Society> {

    private final Map<String, Object> filters;

    public SocietySpecification(Map<String, Object> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<Society> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        // Agregar condiciones a las consultas dinámicas
        if (filters.containsKey("cifDni")) {
            predicates.add(criteriaBuilder.equal(root.get("cifDni"), filters.get("cifDni")));
        }

        if (filters.containsKey("name")) {
            predicates.add(criteriaBuilder.equal(root.get("name"), filters.get("name")));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
