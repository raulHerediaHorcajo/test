package com.example.demo.unit.repository.specification;

import com.example.demo.model.DayWeek;
import com.example.demo.model.Generator;
import com.example.demo.repository.criteria.GeneratorCriteria;
import com.example.demo.repository.specification.GeneratorSpecification;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneratorSpecificationUnitTest {

    private GeneratorSpecification generatorSpecification;

    @Mock
    private GeneratorCriteria filters;
    @Mock
    private Root<Generator> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @BeforeEach
    public void setUp() {
        generatorSpecification = new GeneratorSpecification(filters);
    }

    @Test
    void testToPredicateWithoutFilters() {
        when(filters.isActive()).thenReturn(null);
        when(filters.getPeriodicity()).thenReturn(null);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = new ArrayList<>();

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = generatorSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, never()).like(any(Expression.class), anyString());
        verify(criteriaBuilder, never()).asc(any(Expression.class));
        verify(query, never()).orderBy(any(Order.class));
        verify(criteriaBuilder, never()).equal(any(Expression.class), any(Object.class));
        verify(criteriaBuilder, never()).between(any(Expression.class), any(LocalDate.class), any(LocalDate.class));
        verify(criteriaBuilder, never()).isMember(anyString(), any(Expression.class));
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testToPredicateWithAllFilters() {
        when(filters.getName()).thenReturn("Test Generator");
        when(filters.getSocietyCifDni()).thenReturn("XXXXXXXXXX");
        when(filters.getSocietyName()).thenReturn("Test Society");
        when(filters.getGeneratorTypeName()).thenReturn("Test GeneratorType");
        when(filters.isActive()).thenReturn(true);
        when(filters.getStartInitializationDate()).thenReturn(LocalDate.of(2023, 1, 1));
        when(filters.getFinalInitializationDate()).thenReturn(LocalDate.of(2023, 1, 15));
        when(filters.getStartTerminationDate()).thenReturn(LocalDate.of(2023, 12, 25));
        when(filters.getFinalTerminationDate()).thenReturn(LocalDate.of(2023, 12, 31));
        when(filters.getPeriodicity()).thenReturn(10);
        when(filters.getPickupDay()).thenReturn(DayWeek.MONDAY.getDay());
        when(filters.getOffDay()).thenReturn(DayWeek.SUNDAY.getDay());
        when(filters.getEmail()).thenReturn("test@gmail.com");
        when(filters.getAddress()).thenReturn("C. Tulipán");
        when(filters.getPhoneNumber()).thenReturn("123456789");

        when(root.get(anyString())).thenReturn(mock(Path.class));
        when(root.get(anyString()).get(anyString())).thenReturn(mock(Path.class));

        Predicate predicate = mock(Predicate.class);
        when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(predicate);
        when(criteriaBuilder.equal(any(Expression.class), any(Object.class))).thenReturn(predicate);
        when(criteriaBuilder.between(any(Expression.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(predicate);
        when(criteriaBuilder.isMember(anyString(), any(Expression.class))).thenReturn(predicate);

        Expression<Integer> length = mock(Expression.class);
        when(criteriaBuilder.length(any(Expression.class))).thenReturn(length);
        Order order = mock(Order.class);
        when(criteriaBuilder.asc(length)).thenReturn(order);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = Collections.nCopies(13, predicate);

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = generatorSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, times(8)).like(any(Expression.class), anyString());
        verify(criteriaBuilder, times(8)).asc(any(Expression.class));
        verify(query, times(8)).orderBy(any(Order.class));
        verify(criteriaBuilder, times(2)).equal(any(Expression.class), any(Object.class));
        verify(criteriaBuilder, times(2)).between(any(Expression.class), any(LocalDate.class), any(LocalDate.class));
        verify(criteriaBuilder, times(1)).isMember(anyString(), any(Expression.class));
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("stringAndRelationPredicateScenarios")
    void testToPredicateWithStringAndRelationPredicate(String scenario, Map<String, String> values, List<Integer> expectedTimes) {
        when(filters.isActive()).thenReturn(null);
        when(filters.getPeriodicity()).thenReturn(null);
        when(filters.getName()).thenReturn(values.get("name"));
        when(filters.getSocietyCifDni()).thenReturn(values.get("societyCifDni"));

        Path namePath = mock(Path.class);
        lenient().when(root.get("name")).thenReturn(namePath);
        Path societyCifDniPath = mock(Path.class);
        lenient().when(root.get("society")).thenReturn(mock(Path.class));
        lenient().when(root.get("society").get("cifDni")).thenReturn(societyCifDniPath);

        Predicate nameLikePredicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.like(namePath, "%" + values.get("name") + "%")).thenReturn(nameLikePredicate);
        Predicate societyCifDniLikePredicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.like(societyCifDniPath, "%" + values.get("societyCifDni") + "%")).thenReturn(societyCifDniLikePredicate);

        Expression<Integer> nameLength = mock(Expression.class);
        lenient().when(criteriaBuilder.length(namePath)).thenReturn(nameLength);
        Expression<Integer> societyCifDniLength = mock(Expression.class);
        lenient().when(criteriaBuilder.length(societyCifDniPath)).thenReturn(societyCifDniLength);

        Order nameOrder = mock(Order.class);
        lenient().when(criteriaBuilder.asc(nameLength)).thenReturn(nameOrder);
        Order societyCifDniOrder = mock(Order.class);
        lenient().when(criteriaBuilder.asc(societyCifDniLength)).thenReturn(societyCifDniOrder);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = switch (scenario){
            case "Predicate with Name filter" -> List.of(nameLikePredicate);
            case "Predicate with Society CifDni filter" -> List.of(societyCifDniLikePredicate);
            default -> new ArrayList<>();
        };

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = generatorSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, never()).equal(any(Expression.class), any(Object.class));
        verify(criteriaBuilder, never()).between(any(Expression.class), any(LocalDate.class), any(LocalDate.class));
        verify(criteriaBuilder, never()).isMember(anyString(), any(Expression.class));
        verify(criteriaBuilder, times(expectedTimes.get(0))).like(namePath, "%" + values.get("name") + "%");
        verify(criteriaBuilder, times(expectedTimes.get(0))).asc(nameLength);
        verify(query, times(expectedTimes.get(0))).orderBy(nameOrder);
        verify(criteriaBuilder, times(expectedTimes.get(1))).like(societyCifDniPath, "%" + values.get("societyCifDni") + "%");
        verify(criteriaBuilder, times(expectedTimes.get(1))).asc(societyCifDniLength);
        verify(query, times(expectedTimes.get(1))).orderBy(societyCifDniOrder);
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    private static Stream<Arguments> stringAndRelationPredicateScenarios() {
        return Stream.of(
            arguments("Predicate with Name filter",
                new HashMap<String, String>() {{
                    put("name", "Test Generator");
                    put("societyCifDni", null);
                }},
                List.of(1, 0)
            ),
            arguments("Predicate with Society CifDni filter",
                new HashMap<String, String>() {{
                    put("name", null);
                    put("societyCifDni", "XXXXXXXXXX");
                }},
                List.of(0, 1)
            )
        );
    }

    @Test
    void testToPredicateWithEqualPredicate() {
        when(filters.getPeriodicity()).thenReturn(null);
        when(filters.isActive()).thenReturn(true);

        Path activePath = mock(Path.class);
        when(root.get("active")).thenReturn(activePath);

        Predicate activeEqualPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(activePath, true)).thenReturn(activeEqualPredicate);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = List.of(activeEqualPredicate);

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = generatorSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, never()).like(any(Expression.class), anyString());
        verify(criteriaBuilder, never()).asc(any(Expression.class));
        verify(query, never()).orderBy(any(Order.class));
        verify(criteriaBuilder, never()).between(any(Expression.class), any(LocalDate.class), any(LocalDate.class));
        verify(criteriaBuilder, never()).isMember(anyString(), any(Expression.class));
        verify(criteriaBuilder).equal(activePath, true);
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dateRangePredicateScenarios")
    void testToPredicateWithDateRangePredicate(String scenario, Map<String, LocalDate> values, Map<String, LocalDate> actualDates) {
        when(filters.isActive()).thenReturn(null);
        when(filters.getPeriodicity()).thenReturn(null);
        when(filters.getStartInitializationDate()).thenReturn(values.get("startInitializationDate"));
        when(filters.getFinalInitializationDate()).thenReturn(values.get("finalInitializationDate"));

        Path initializationDatePath = mock(Path.class);
        when(root.get("initializationDate")).thenReturn(initializationDatePath);

        Predicate initializationDateBetweenPredicate = mock(Predicate.class);
        when(criteriaBuilder.between(initializationDatePath, actualDates.get("startInitializationDate"), actualDates.get("finalInitializationDate"))).thenReturn(initializationDateBetweenPredicate);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = List.of(initializationDateBetweenPredicate);

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = generatorSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, never()).like(any(Expression.class), anyString());
        verify(criteriaBuilder, never()).asc(any(Expression.class));
        verify(query, never()).orderBy(any(Order.class));
        verify(criteriaBuilder, never()).equal(any(Expression.class), any(Object.class));
        verify(criteriaBuilder, never()).isMember(anyString(), any(Expression.class));
        verify(criteriaBuilder).between(initializationDatePath, actualDates.get("startInitializationDate"), actualDates.get("finalInitializationDate"));
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    private static Stream<Arguments> dateRangePredicateScenarios() {
        return Stream.of(
            arguments("Predicate with Initialization Date filter with only Start Date",
                new HashMap<String, LocalDate>() {{
                    put("startInitializationDate", LocalDate.of(2023, 1, 1));
                    put("finalInitializationDate", null);
                }},
                new HashMap<String, LocalDate>() {{
                    put("startInitializationDate", LocalDate.of(2023, 1, 1));
                    put("finalInitializationDate", LocalDate.now());
                }}
            ),
            arguments("Predicate with Initialization Date filter with only Final Date",
                new HashMap<String, LocalDate>() {{
                    put("startInitializationDate", null);
                    put("finalInitializationDate", LocalDate.of(2023, 1, 15));
                }},
                new HashMap<String, LocalDate>() {{
                    put("startInitializationDate", LocalDate.MIN);
                    put("finalInitializationDate", LocalDate.of(2023, 1, 15));
                }}
            ),
            arguments("Predicate with Initialization Date filter with Start and Final Dates",
                new HashMap<String, LocalDate>() {{
                    put("startInitializationDate", LocalDate.of(2023, 1, 1));
                    put("finalInitializationDate", LocalDate.of(2023, 1, 15));
                }},
                new HashMap<String, LocalDate>() {{
                    put("startInitializationDate", LocalDate.of(2023, 1, 1));
                    put("finalInitializationDate", LocalDate.of(2023, 1, 15));
                }}
            )
        );
    }

    @Test
    void testToPredicateWithIsMemberPredicate() {
        when(filters.isActive()).thenReturn(null);
        when(filters.getPeriodicity()).thenReturn(null);
        when(filters.getPhoneNumber()).thenReturn("123456789");

        Path phoneNumberPath = mock(Path.class);
        when(root.get("phoneNumber")).thenReturn(phoneNumberPath);

        Predicate phoneNumberIsMemberPredicate = mock(Predicate.class);
        when(criteriaBuilder.isMember("123456789", phoneNumberPath)).thenReturn(phoneNumberIsMemberPredicate);

        Predicate conjuntionPredicate = mock(Predicate.class);
        List<Predicate> expectedPredicates = List.of(phoneNumberIsMemberPredicate);

        when(criteriaBuilder.and(expectedPredicates.toArray(new Predicate[0]))).thenReturn(conjuntionPredicate);

        Predicate result = generatorSpecification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, never()).like(any(Expression.class), anyString());
        verify(criteriaBuilder, never()).asc(any(Expression.class));
        verify(query, never()).orderBy(any(Order.class));
        verify(criteriaBuilder, never()).equal(any(Expression.class), any(Object.class));
        verify(criteriaBuilder, never()).between(any(Expression.class), any(LocalDate.class), any(LocalDate.class));
        verify(criteriaBuilder).isMember("123456789", phoneNumberPath);
        verify(criteriaBuilder).and(expectedPredicates.toArray(new Predicate[0]));
        assertEquals(conjuntionPredicate, result);
    }

    @Test
    void testEqualsAndHashCode() {
        GeneratorSpecification duplicatedSocietySpecification = new GeneratorSpecification(filters);
        assertThat(generatorSpecification.equals(duplicatedSocietySpecification)).isTrue();
        assertEquals(generatorSpecification.hashCode(), duplicatedSocietySpecification.hashCode());

        GeneratorSpecification distinctSocietySpecification = new GeneratorSpecification(mock(GeneratorCriteria.class));
        assertThat(generatorSpecification.equals(distinctSocietySpecification)).isFalse();
        assertNotEquals(generatorSpecification.hashCode(), distinctSocietySpecification.hashCode());

        assertThat(generatorSpecification.equals(generatorSpecification)).isTrue();
        assertThat(generatorSpecification.equals(null)).isFalse();
        assertThat(generatorSpecification.equals(new Object())).isFalse();
        assertThat(generatorSpecification.equals(mock(GeneratorSpecification.class))).isFalse();
    }
}