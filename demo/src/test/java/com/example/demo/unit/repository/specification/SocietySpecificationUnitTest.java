package com.example.demo.unit.repository.specification;

import com.example.demo.model.Society;
import com.example.demo.repository.criteria.SocietyCriteria;
import com.example.demo.repository.specification.SocietySpecification;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocietySpecificationUnitTest {

    private SocietySpecification societySpecification;

    @Mock
    private SocietyCriteria filters;
    @Mock
    private Root<Society> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @BeforeEach
    public void setUp() {
        societySpecification = new SocietySpecification(filters);
    }

    @Test
    void testToPredicateWithoutFilters() {
        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = new ArrayList<>();

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = societySpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, never()).like(any(Expression.class), anyString());
        verify(criteriaBuilder, never()).asc(any(Expression.class));
        verify(query, never()).orderBy(any(Order.class));
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testToPredicateWithAllFilters() {
        when(filters.getCifDni()).thenReturn("XXXXXXXXXX");
        when(filters.getName()).thenReturn("Test Society");

        when(root.get(anyString())).thenReturn(mock(Path.class));

        Predicate predicate = mock(Predicate.class);
        when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(predicate);

        Expression<Integer> length = mock(Expression.class);
        when(criteriaBuilder.length(any(Expression.class))).thenReturn(length);
        Order order = mock(Order.class);
        when(criteriaBuilder.asc(length)).thenReturn(order);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = Collections.nCopies(2, predicate);

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = societySpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, times(2)).like(any(Expression.class), anyString());
        verify(criteriaBuilder, times(2)).asc(any(Expression.class));
        verify(query, times(2)).orderBy(any(Order.class));
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testToPredicateWithStringPredicate() {
        when(filters.getCifDni()).thenReturn("XXXXXXXXXX");

        Path cifDniPath = mock(Path.class);
        when(root.get("cifDni")).thenReturn(cifDniPath);

        Predicate cifDniLikePredicate = mock(Predicate.class);
        when(criteriaBuilder.like(cifDniPath, "%" + "XXXXXXXXXX" + "%")).thenReturn(cifDniLikePredicate);

        Expression<Integer> cifDniLength = mock(Expression.class);
        when(criteriaBuilder.length(cifDniPath)).thenReturn(cifDniLength);
        Order cifDniOrder = mock(Order.class);
        when(criteriaBuilder.asc(cifDniLength)).thenReturn(cifDniOrder);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = List.of(cifDniLikePredicate);

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = societySpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder).like(cifDniPath, "%" + "XXXXXXXXXX" + "%");
        verify(criteriaBuilder).asc(cifDniLength);
        verify(query).orderBy(cifDniOrder);
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testEqualsAndHashCode() {
        SocietySpecification duplicatedSocietySpecification = new SocietySpecification(filters);
        assertThat(societySpecification.equals(duplicatedSocietySpecification)).isTrue();
        assertEquals(societySpecification.hashCode(), duplicatedSocietySpecification.hashCode());

        SocietySpecification distinctSocietySpecification = new SocietySpecification(mock(SocietyCriteria.class));
        assertThat(societySpecification.equals(distinctSocietySpecification)).isFalse();
        assertNotEquals(societySpecification.hashCode(), distinctSocietySpecification.hashCode());

        assertThat(societySpecification.equals(societySpecification)).isTrue();
        assertThat(societySpecification.equals(null)).isFalse();
        assertThat(societySpecification.equals(new Object())).isFalse();
        assertThat(societySpecification.equals(mock(SocietySpecification.class))).isFalse();
    }
}