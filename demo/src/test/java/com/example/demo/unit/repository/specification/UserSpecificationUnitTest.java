package com.example.demo.unit.repository.specification;

import com.example.demo.model.User;
import com.example.demo.repository.criteria.UserCriteria;
import com.example.demo.repository.specification.UserSpecification;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void testToPredicate(String scenario, String name, String email, List<String> roles, List<Integer> expectedTimes) {
        when(filters.getName()).thenReturn(name);
        when(filters.getEmail()).thenReturn(email);
        when(filters.getRoles()).thenReturn(roles);

        Path namePath = mock(Path.class);
        lenient().when(root.get("name")).thenReturn(namePath);
        Path emailPath = mock(Path.class);
        lenient().when(root.get("email")).thenReturn(emailPath);
        Path rolesPath = mock(Path.class);
        lenient().when(root.get("roles")).thenReturn(rolesPath);

        Expression<String> nameLower = mock(Expression.class);
        lenient().when(criteriaBuilder.lower(namePath)).thenReturn(nameLower);
        Expression<String> emailLower = mock(Expression.class);
        lenient().when(criteriaBuilder.lower(emailPath)).thenReturn(emailLower);


        String nameLike = (filters.getName() != null) ?
            "%" + filters.getName().toLowerCase() + "%" : null;
        String emailLike = (filters.getEmail() != null) ?
            "%" + filters.getEmail().toLowerCase() + "%" : null;

        Predicate namePredicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.like(nameLower, nameLike)).thenReturn(namePredicate);
        Predicate emailPredicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.like(emailLower, emailLike)).thenReturn(emailPredicate);
        List<Predicate> rolePredicates = new ArrayList<>();
        for (String role : (roles != null ? filters.getRoles() : Collections.<String>emptyList())) {
            Predicate rolePredicate = mock(Predicate.class);
            lenient().when(criteriaBuilder.isMember(role.toUpperCase(), rolesPath)).thenReturn(rolePredicate);
            rolePredicates.add(rolePredicate);
        }
        Predicate rolesConjuntionPredicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.and(rolePredicates.toArray(new Predicate[0]))).thenReturn(rolesConjuntionPredicate);

        Expression<Integer> nameLength = mock(Expression.class);
        lenient().when(criteriaBuilder.length(namePath)).thenReturn(nameLength);
        Expression<Integer> emailLength = mock(Expression.class);
        lenient().when(criteriaBuilder.length(emailPath)).thenReturn(emailLength);

        Order nameOrder = mock(Order.class);
        lenient().when(criteriaBuilder.asc(nameLength)).thenReturn(nameOrder);
        Order emailOrder = mock(Order.class);
        lenient().when(criteriaBuilder.asc(emailLength)).thenReturn(emailOrder);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = switch (scenario){
            case "Predicate without filters" -> new ArrayList<>();
            case "Predicate with Name filter" -> List.of(namePredicate);
            case "Predicate with Email filter" -> List.of(emailPredicate);
            case "Predicate with Roles filter" -> List.of(rolesConjuntionPredicate);
            default -> List.of(namePredicate, emailPredicate, rolesConjuntionPredicate);
        };

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = userSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, times(expectedTimes.get(0))).like(nameLower, nameLike);
        verify(criteriaBuilder, times(expectedTimes.get(1))).like(emailLower, emailLike);
        verify(criteriaBuilder, times(expectedTimes.get(0))).asc(nameLength);
        verify(criteriaBuilder, times(expectedTimes.get(1))).asc(emailLength);
        verify(query, times(expectedTimes.get(0))).orderBy(nameOrder);
        verify(query, times(expectedTimes.get(1))).orderBy(emailOrder);
        verify(criteriaBuilder, times(expectedTimes.get(2))).and(rolePredicates.toArray(new Predicate[0]));
        verify(criteriaBuilder, times(expectedTimes.get(3))).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("Predicate without filters",
                null,
                null,
                null,
                List.of(0, 0, 1, 1)
                ),
            arguments("Predicate with Name filter",
                "Test User",
                null,
                List.of(),
                List.of(1, 0, 0, 1)
            ),
            arguments("Predicate with Email filter",
                null,
                "test@gmail.com",
                List.of(),
                List.of(0, 1, 0,  1)
            ),
            arguments("Predicate with Roles filter",
                null,
                null,
                List.of("ADMIN", "USER"),
                List.of(0, 0, 1, 1)
            ),
            arguments("Predicate with all filters",
                "Test User",
                "test@gmail.com",
                List.of("ADMIN", "USER"),
                List.of(1, 1, 1, 1)
            )
        );
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
