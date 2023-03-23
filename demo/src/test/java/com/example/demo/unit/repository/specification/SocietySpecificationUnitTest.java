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

        Path<Object> cifDniPath = mock(Path.class);
        lenient().when(root.get("cifDni")).thenReturn(cifDniPath);
        Path<Object> namePath = mock(Path.class);
        lenient().when(root.get("name")).thenReturn(namePath);

        Predicate cifDniPredicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.equal(cifDniPath, filters.getCifDni())).thenReturn(cifDniPredicate);
        Predicate namePredicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.equal(namePath, filters.getName())).thenReturn(namePredicate);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = switch (scenario){
            case "Predicate without filters" -> new ArrayList<>();
            case "Predicate with CifDni filter" -> List.of(cifDniPredicate);
            case "Predicate with Name filter" -> List.of(namePredicate);
            default -> List.of(cifDniPredicate, namePredicate);
        };

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = societySpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, times(expectedTimes.get(0))).equal(cifDniPath, filters.getCifDni());
        verify(criteriaBuilder, times(expectedTimes.get(1))).equal(namePath, filters.getName());
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
        SocietySpecification duplicatedSociety = new SocietySpecification(filters);
        assertThat(societySpecification.equals(duplicatedSociety)).isTrue();
        assertEquals(societySpecification.hashCode(), duplicatedSociety.hashCode());

        /*Society differentSocietyId = new Society(2, "XXXXXXXXXX", "Test Society");
        assertThat(society.equals(differentSocietyId)).isFalse();
        Society differentSocietyCifDni = new Society(1, "YYYYYYYYYY", "Test Society");
        assertThat(society.equals(differentSocietyCifDni)).isFalse();
        Society differentSocietyName = new Society(1, "XXXXXXXXXX", "Distinct Society");
        assertThat(society.equals(differentSocietyName)).isFalse();

        Society distinctSociety = new Society(2, "YYYYYYYYYY", "Distinct Society");
        assertThat(society.equals(distinctSociety)).isFalse();
        assertNotEquals(society.hashCode(), distinctSociety.hashCode());

        assertThat(society.equals(society)).isTrue();
        assertThat(society.equals(null)).isFalse();
        assertThat(society.equals(new Object())).isFalse();
        assertThat(society.equals(mock(Society.class))).isFalse();*/
    }
}
