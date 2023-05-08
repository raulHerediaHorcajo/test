package com.example.demo.unit.repository.specification;

import com.example.demo.model.Society;
import com.example.demo.repository.criteria.SocietyCriteria;
import com.example.demo.repository.specification.SocietySpecification;
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
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void testToPredicate(String scenario, String cifDni, String name, List<Integer> expectedTimes) {
        when(filters.getCifDni()).thenReturn(cifDni);
        when(filters.getName()).thenReturn(name);

        Path cifDniPath = mock(Path.class);
        lenient().when(root.get("cifDni")).thenReturn(cifDniPath);
        Path namePath = mock(Path.class);
        lenient().when(root.get("name")).thenReturn(namePath);

        Expression<String> cifDniLower = mock(Expression.class);
        lenient().when(criteriaBuilder.lower(cifDniPath)).thenReturn(cifDniLower);
        Expression<String> nameLower = mock(Expression.class);
        lenient().when(criteriaBuilder.lower(namePath)).thenReturn(nameLower);

        String cifDniLike = (filters.getCifDni() != null) ?
            "%" + filters.getCifDni().toLowerCase() + "%" : null;

        String nameLike = (filters.getName() != null) ?
            "%" + filters.getName().toLowerCase() + "%" : null;

        Predicate cifDniPredicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.like(cifDniLower, cifDniLike)).thenReturn(cifDniPredicate);
        Predicate namePredicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.like(nameLower, nameLike)).thenReturn(namePredicate);

        Expression<Integer> cifDniLength = mock(Expression.class);
        lenient().when(criteriaBuilder.length(cifDniPath)).thenReturn(cifDniLength);
        Expression<Integer> nameLength = mock(Expression.class);
        lenient().when(criteriaBuilder.length(namePath)).thenReturn(nameLength);

        Order cifDniOrder = mock(Order.class);
        lenient().when(criteriaBuilder.asc(cifDniLength)).thenReturn(cifDniOrder);
        Order nameOrder = mock(Order.class);
        lenient().when(criteriaBuilder.asc(nameLength)).thenReturn(nameOrder);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = switch (scenario){
            case "Predicate without filters" -> new ArrayList<>();
            case "Predicate with CifDni filter" -> List.of(cifDniPredicate);
            case "Predicate with Name filter" -> List.of(namePredicate);
            default -> List.of(cifDniPredicate, namePredicate);
        };

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = societySpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, times(expectedTimes.get(0))).like(cifDniLower, cifDniLike);
        verify(criteriaBuilder, times(expectedTimes.get(1))).like(nameLower, nameLike);
        verify(criteriaBuilder, times(expectedTimes.get(0))).asc(cifDniLength);
        verify(criteriaBuilder, times(expectedTimes.get(1))).asc(nameLength);
        verify(query, times(expectedTimes.get(0))).orderBy(cifDniOrder);
        verify(query, times(expectedTimes.get(1))).orderBy(nameOrder);
        verify(criteriaBuilder, times(expectedTimes.get(2))).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("Predicate without filters",
                null,
                null,
                List.of(0, 0, 1)
                ),
            arguments("Predicate with CifDni filter",
                "XXXXXXXXXX",
                null,
                List.of(1, 0, 1)
            ),
            arguments("Predicate with Name filter",
                null,
                "Test Society",
                List.of(0, 1, 1)
            ),
            arguments("Predicate with all filters",
                "XXXXXXXXXX",
                "Test Society",
                List.of(1, 1, 1)
            )
        );
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
