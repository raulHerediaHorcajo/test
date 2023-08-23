package com.example.demo.repository.specification;

import com.example.demo.model.Generator;
import com.example.demo.repository.criteria.GeneratorCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class GeneratorSpecification implements Specification<Generator> {

    private final transient GeneratorCriteria filters;

    public GeneratorSpecification(GeneratorCriteria filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<Generator> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        // Agregar condiciones a las consultas dinámicas
        BiConsumer<String, String> addStringPredicate = (attribute, value) -> {
            if (value != null) {
                predicates.add(criteriaBuilder.like(root.get(attribute), "%" + value + "%"));
                query.orderBy(criteriaBuilder.asc(criteriaBuilder.length(root.get(attribute))));
            }
        };

        TriConsumer<String, String, String> addRelationPredicate = (attribute1, attribute2, value) -> {
            if (value != null) {
                predicates.add(criteriaBuilder.like(root.get(attribute1).get(attribute2), "%" + value + "%"));
                query.orderBy(criteriaBuilder.asc(criteriaBuilder.length(root.get(attribute1).get(attribute2))));
            }
        };

        BiConsumer<String, Object> addEqualPredicate = (attribute, value) -> {
            if (value != null) {
                predicates.add(criteriaBuilder.equal(root.get(attribute), value));
            }
        };

        TriConsumer<String, LocalDate, LocalDate> addDateRangePredicate = (attribute, startDate, finalDate) -> {
            LocalDate actualStartDate = startDate != null ? startDate : LocalDate.MIN;
            LocalDate actualFinalDate = finalDate != null ? finalDate : LocalDate.now();

            if (startDate != null || finalDate != null) {
                predicates.add(criteriaBuilder.between(root.get(attribute), actualStartDate, actualFinalDate));
            }
        };

        addStringPredicate.accept("name", filters.getName());
        addRelationPredicate.accept("society", "cifDni", filters.getSocietyCifDni());
        addRelationPredicate.accept("society", "name", filters.getSocietyName());
        addRelationPredicate.accept("generatorType", "name", filters.getGeneratorTypeName());
        addEqualPredicate.accept("active", filters.isActive());
        addDateRangePredicate.accept("initializationDate", filters.getStartInitializationDate(), filters.getFinalInitializationDate());
        addDateRangePredicate.accept("terminationDate", filters.getStartTerminationDate(), filters.getFinalTerminationDate());
        addEqualPredicate.accept("periodicity", filters.getPeriodicity());
        addStringPredicate.accept("pickupDay", filters.getPickupDay());
        addStringPredicate.accept("offDay", filters.getOffDay());
        addStringPredicate.accept("email", filters.getEmail());
        addStringPredicate.accept("address", filters.getAddress());

        if (filters.getPhoneNumber() != null) {
            predicates.add(criteriaBuilder.isMember(filters.getPhoneNumber(), root.get("phoneNumber")));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneratorSpecification that)) return false;
        return Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters);
    }
}