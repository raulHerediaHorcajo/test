package com.example.demo.unit.repository.specification;

import com.example.demo.model.GeneratorType;
import com.example.demo.repository.criteria.GeneratorTypeCriteria;
import com.example.demo.repository.specification.GeneratorTypeSpecification;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneratorTypeSpecificationUnitTest {

    private GeneratorTypeSpecification generatorTypeSpecification;

    @Mock
    private GeneratorTypeCriteria filters;
    @Mock
    private Root<GeneratorType> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @BeforeEach
    public void setUp() {
        generatorTypeSpecification = new GeneratorTypeSpecification(filters);
    }

    @Test
    void testToPredicateWithoutFilters() {
        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = new ArrayList<>();

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = generatorTypeSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, never()).like(any(Expression.class), anyString());
        verify(criteriaBuilder, never()).asc(any(Expression.class));
        verify(query, never()).orderBy(any(Order.class));
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testToPredicateWithAllFilters() {
        when(filters.getName()).thenReturn("Test GeneratorType");

        when(root.get(anyString())).thenReturn(mock(Path.class));

        Predicate predicate = mock(Predicate.class);
        when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(predicate);

        Expression<Integer> length = mock(Expression.class);
        when(criteriaBuilder.length(any(Expression.class))).thenReturn(length);
        Order order = mock(Order.class);
        when(criteriaBuilder.asc(length)).thenReturn(order);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = List.of(predicate);

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = generatorTypeSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder).like(any(Expression.class), anyString());
        verify(criteriaBuilder).asc(any(Expression.class));
        verify(query).orderBy(any(Order.class));
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testToPredicateWithStringPredicate() {
        when(filters.getName()).thenReturn("Test GeneratorType");

        Path namePath = mock(Path.class);
        when(root.get("name")).thenReturn(namePath);

        Predicate nameLikePredicate = mock(Predicate.class);
        when(criteriaBuilder.like(namePath, "%" + "Test GeneratorType" + "%")).thenReturn(nameLikePredicate);

        Expression<Integer> nameLength = mock(Expression.class);
        when(criteriaBuilder.length(namePath)).thenReturn(nameLength);
        Order nameOrder = mock(Order.class);
        when(criteriaBuilder.asc(nameLength)).thenReturn(nameOrder);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = List.of(nameLikePredicate);

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = generatorTypeSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder).like(namePath, "%" + "Test GeneratorType" + "%");
        verify(criteriaBuilder).asc(nameLength);
        verify(query).orderBy(nameOrder);
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testEqualsAndHashCode() {
        GeneratorTypeSpecification duplicatedGeneratorTypeSpecification = new GeneratorTypeSpecification(filters);
        assertThat(generatorTypeSpecification.equals(duplicatedGeneratorTypeSpecification)).isTrue();
        assertEquals(generatorTypeSpecification.hashCode(), duplicatedGeneratorTypeSpecification.hashCode());

        GeneratorTypeSpecification distinctGeneratorTypeSpecification = new GeneratorTypeSpecification(mock(GeneratorTypeCriteria.class));
        assertThat(generatorTypeSpecification.equals(distinctGeneratorTypeSpecification)).isFalse();
        assertNotEquals(generatorTypeSpecification.hashCode(), distinctGeneratorTypeSpecification.hashCode());

        assertThat(generatorTypeSpecification.equals(generatorTypeSpecification)).isTrue();
        assertThat(generatorTypeSpecification.equals(null)).isFalse();
        assertThat(generatorTypeSpecification.equals(new Object())).isFalse();
        assertThat(generatorTypeSpecification.equals(mock(GeneratorTypeSpecification.class))).isFalse();
    }
}
