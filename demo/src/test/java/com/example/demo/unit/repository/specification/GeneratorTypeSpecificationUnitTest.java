package com.example.demo.unit.repository.specification;

import com.example.demo.model.GeneratorType;
import com.example.demo.repository.criteria.GeneratorTypeCriteria;
import com.example.demo.repository.specification.GeneratorTypeSpecification;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void testToPredicate(String scenario, String name, List<Integer> expectedTimes) {
        when(filters.getName()).thenReturn(name);

        Path namePath = mock(Path.class);
        lenient().when(root.get("name")).thenReturn(namePath);

        Expression<String> nameLower = mock(Expression.class);
        lenient().when(criteriaBuilder.lower(namePath)).thenReturn(nameLower);

        String nameLike = (filters.getName() != null) ?
            "%" + filters.getName().toLowerCase() + "%" : null;

        Predicate namePredicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.like(nameLower, nameLike)).thenReturn(namePredicate);

        Expression<Integer> nameLength = mock(Expression.class);
        lenient().when(criteriaBuilder.length(namePath)).thenReturn(nameLength);

        Order nameOrder = mock(Order.class);
        lenient().when(criteriaBuilder.asc(nameLength)).thenReturn(nameOrder);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = switch (scenario){
            case "Predicate without filters" -> new ArrayList<>();
            case "Predicate with Name filter" -> List.of(namePredicate);
            default -> List.of(namePredicate);
        };

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = generatorTypeSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, times(expectedTimes.get(1))).like(nameLower, nameLike);
        verify(criteriaBuilder, times(expectedTimes.get(1))).asc(nameLength);
        verify(query, times(expectedTimes.get(1))).orderBy(nameOrder);
        verify(criteriaBuilder, times(expectedTimes.get(2))).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("Predicate without filters",
                null,
                List.of(0, 0, 1)
                ),
            arguments("Predicate with Name filter",
                "Test GeneratorType",
                List.of(0, 1, 1)
            ),
            arguments("Predicate with all filters",
                "Test GeneratorType",
                List.of(1, 1, 1)
            )
        );
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
