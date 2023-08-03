package com.example.demo.unit.repository.specification;

import com.example.demo.model.User;
import com.example.demo.repository.criteria.UserCriteria;
import com.example.demo.repository.specification.UserSpecification;
import com.example.demo.security.config.SecurityExpressions.UserRole;
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
class UserSpecificationUnitTest {

    private UserSpecification userSpecification;

    @Mock
    private UserCriteria filters;
    @Mock
    private Root<User> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @BeforeEach
    public void setUp() {
        userSpecification = new UserSpecification(filters);
    }

    @Test
    void testToPredicateWithoutFilters() {
        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = new ArrayList<>();

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = userSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, never()).like(any(Expression.class), anyString());
        verify(criteriaBuilder, never()).asc(any(Expression.class));
        verify(query, never()).orderBy(any(Order.class));
        verify(criteriaBuilder, never()).isMember(anyString(), any(Expression.class));
        verify(criteriaBuilder, times(1)).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testToPredicateWithAllFilters() {
        when(filters.getName()).thenReturn("Test User");
        when(filters.getEmail()).thenReturn("test@gmail.com");
        when(filters.getRoles()).thenReturn(List.of(UserRole.ADMIN.name(), UserRole.USER.name()));

        when(root.get(anyString())).thenReturn(mock(Path.class));

        Predicate predicate = mock(Predicate.class);
        when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(predicate);

        Predicate rolesPredicate = mock(Predicate.class);
        when(criteriaBuilder.isMember(anyString(), any(Expression.class))).thenReturn(rolesPredicate);
        List<Predicate> rolesPredicates = Collections.nCopies(2, rolesPredicate);
        when(criteriaBuilder.and(rolesPredicates.toArray(new Predicate[0]))).thenReturn(predicate);

        Expression<Integer> length = mock(Expression.class);
        when(criteriaBuilder.length(any(Expression.class))).thenReturn(length);
        Order order = mock(Order.class);
        when(criteriaBuilder.asc(length)).thenReturn(order);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = Collections.nCopies(3, predicate);
        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = userSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, times(2)).like(any(Expression.class), anyString());
        verify(criteriaBuilder, times(2)).asc(any(Expression.class));
        verify(query, times(2)).orderBy(any(Order.class));
        verify(criteriaBuilder, times(2)).isMember(anyString(), any(Expression.class));
        verify(criteriaBuilder).and(rolesPredicates.toArray(new Predicate[0]));
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testToPredicateWithStringPredicate() {
        when(filters.getName()).thenReturn("Test User");

        Path namePath = mock(Path.class);
        when(root.get("name")).thenReturn(namePath);

        Predicate nameLikePredicate = mock(Predicate.class);
        when(criteriaBuilder.like(namePath, "%" + "Test User" + "%")).thenReturn(nameLikePredicate);

        Expression<Integer> nameLength = mock(Expression.class);
        when(criteriaBuilder.length(namePath)).thenReturn(nameLength);
        Order nameOrder = mock(Order.class);
        when(criteriaBuilder.asc(nameLength)).thenReturn(nameOrder);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = List.of(nameLikePredicate);

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = userSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, never()).isMember(anyString(), any(Expression.class));
        verify(criteriaBuilder, never()).and(new ArrayList<>().toArray(new Predicate[0]));
        verify(criteriaBuilder).like(namePath, "%" + "Test User" + "%");
        verify(criteriaBuilder).asc(nameLength);
        verify(query).orderBy(nameOrder);
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testToPredicateWithRolesPredicate() {
        when(filters.getRoles()).thenReturn(List.of(UserRole.ADMIN.name(), UserRole.USER.name()));

        Path rolesPath = mock(Path.class);
        when(root.get("roles")).thenReturn(rolesPath);

        Predicate rolesIsMemberPredicate = mock(Predicate.class);
        when(criteriaBuilder.isMember(UserRole.ADMIN.name(), rolesPath)).thenReturn(rolesIsMemberPredicate);
        when(criteriaBuilder.isMember(UserRole.USER.name(), rolesPath)).thenReturn(rolesIsMemberPredicate);
        List<Predicate> rolePredicates = Collections.nCopies(2, rolesIsMemberPredicate);
        Predicate rolesConjuntionPredicate = mock(Predicate.class);
        when(criteriaBuilder.and(rolePredicates.toArray(new Predicate[0]))).thenReturn(rolesConjuntionPredicate);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = List.of(rolesConjuntionPredicate);

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = userSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, never()).like(any(Expression.class), anyString());
        verify(criteriaBuilder, never()).asc(any(Expression.class));
        verify(query, never()).orderBy(any(Order.class));
        verify(criteriaBuilder, times(2)).isMember(anyString(), any(Expression.class));
        verify(criteriaBuilder).and(rolePredicates.toArray(new Predicate[0]));
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testEqualsAndHashCode() {
        UserSpecification duplicatedSocietySpecification = new UserSpecification(filters);
        assertThat(userSpecification.equals(duplicatedSocietySpecification)).isTrue();
        assertEquals(userSpecification.hashCode(), duplicatedSocietySpecification.hashCode());

        UserSpecification distinctSocietySpecification = new UserSpecification(mock(UserCriteria.class));
        assertThat(userSpecification.equals(distinctSocietySpecification)).isFalse();
        assertNotEquals(userSpecification.hashCode(), distinctSocietySpecification.hashCode());

        assertThat(userSpecification.equals(userSpecification)).isTrue();
        assertThat(userSpecification.equals(null)).isFalse();
        assertThat(userSpecification.equals(new Object())).isFalse();
        assertThat(userSpecification.equals(mock(UserSpecification.class))).isFalse();
    }
}