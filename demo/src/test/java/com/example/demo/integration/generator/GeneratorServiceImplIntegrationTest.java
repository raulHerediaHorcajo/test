package com.example.demo.integration.generator;

import com.example.demo.model.DayWeek;
import com.example.demo.model.Generator;
import com.example.demo.model.GeneratorType;
import com.example.demo.model.Society;
import com.example.demo.repository.GeneratorRepository;
import com.example.demo.repository.criteria.GeneratorCriteria;
import com.example.demo.service.GeneratorServiceImpl;
import com.example.demo.service.GeneratorTypeServiceImpl;
import com.example.demo.service.SocietyServiceImpl;
import com.example.demo.util.builder.GeneratorBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class GeneratorServiceImplIntegrationTest {

    @Autowired
    private SocietyServiceImpl societyServiceImpl;
    @Autowired
    private GeneratorTypeServiceImpl generatorTypeServiceImpl;
    @Autowired
    private GeneratorServiceImpl generatorServiceImpl;
    @Autowired
    private GeneratorRepository generatorRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void testFindAll(String scenario, GeneratorCriteria filters, List<Generator> expectedGenerators, String retrieveSql) {
        societyServiceImpl.addSociety(new Society("XXXXXXXXXX", "Test Society"));
        generatorTypeServiceImpl.addGeneratorType(new GeneratorType("Test GeneratorType"));
        Society storedSociety2 = societyServiceImpl.addSociety(new Society("YYYYYYYYYY", "Test Society 2"));
        GeneratorType storedGeneratorType2 = generatorTypeServiceImpl.addGeneratorType(new GeneratorType("Test GeneratorType 2"));
        List<Generator> generators = List.of(
            new GeneratorBuilder().withName("Test Generator 1")
                .withPhoneNumber(List.of("111111111")).build(),
            new GeneratorBuilder()
                .withName("Test Generator 2")
                .withSociety(storedSociety2)
                .withActive(false).withAddress("C. Tulipán 2").build(),
            new GeneratorBuilder()
                .withName("Test Generator 3")
                .withGeneratorType(storedGeneratorType2)
                .withInitializationDate(LocalDate.of(2023, 6, 1))
                .withAddress("C. Tulipán 3").withPhoneNumber(List.of("111111111")).build()
        );
        generatorRepository.saveAll(generators);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Generator> result = generatorServiceImpl.findAll(filters, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isEqualTo(expectedGenerators.size());
        assertThat(result.getContent())
            .containsAll(expectedGenerators);
        assertThat(result.getPageable()).isEqualTo(pageable);

        List<Generator> retrievedGenerators = jdbcTemplate.query(retrieveSql, JdbcTemplateMapperFactory
            .newInstance()
            .addKeys("id")
            .newResultSetExtractor(Generator.class));
        assertThat(retrievedGenerators).containsAll(expectedGenerators);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("FindAll without filters",
                new GeneratorCriteria(null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null),
                List.of(
                    new GeneratorBuilder().withId(1).withName("Test Generator 1")
                        .withPhoneNumber(List.of("111111111")).build(),
                    new GeneratorBuilder()
                        .withId(2).withName("Test Generator 2")
                        .withSociety(new Society(2,"YYYYYYYYYY","Test Society 2"))
                        .withActive(false).withAddress("C. Tulipán 2").build(),
                    new GeneratorBuilder()
                        .withId(3).withName("Test Generator 3")
                        .withGeneratorType(new GeneratorType(2,"Test GeneratorType 2"))
                        .withInitializationDate(LocalDate.of(2023, 6, 1))
                        .withAddress("C. Tulipán 3").withPhoneNumber(List.of("111111111")).build()
                ),
                """
                    SELECT G.id, G.name, G.active, G.initialization_date, G.termination_date,
                    G.periodicity, G.pickup_day, G.off_day, G.opening_time, G.closing_time,
                    G.email, G.address, G.observations, S.id AS society_id, S.name AS society_name,
                    S.cif_dni AS society_cif_dni, GT.id AS generator_type_id, GT.name AS generator_type_name,
                    GPN.phone_number
                    FROM generator G
                    JOIN society S ON G.society_id = S.id
                    JOIN generator_type GT ON G.generator_type_id = GT.id
                    JOIN generator_phone_number GPN ON G.id = GPN.generator_id"""
            ),
            arguments("FindAll with String filter",
                new GeneratorCriteria("Test Generator 1", null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null),
                List.of(
                    new GeneratorBuilder().withId(1).withName("Test Generator 1")
                        .withPhoneNumber(List.of("111111111")).build()
                ),
                """
                    SELECT G.id, G.name, G.active, G.initialization_date, G.termination_date,
                    G.periodicity, G.pickup_day, G.off_day, G.opening_time, G.closing_time,
                    G.email, G.address, G.observations, S.id AS society_id, S.name AS society_name,
                    S.cif_dni AS society_cif_dni, GT.id AS generator_type_id, GT.name AS generator_type_name,
                    GPN.phone_number
                    FROM generator G
                    JOIN society S ON G.society_id = S.id
                    JOIN generator_type GT ON G.generator_type_id = GT.id
                    JOIN generator_phone_number GPN ON G.id = GPN.generator_id
                    WHERE G.name = 'Test Generator 1'"""
            ),
            arguments("FindAll with Partial String and Case Insensitivity filter",
                new GeneratorCriteria("GENERator 1", null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null),
                List.of(
                    new GeneratorBuilder().withId(1).withName("Test Generator 1")
                        .withPhoneNumber(List.of("111111111")).build()
                ),
                """
                    SELECT G.id, G.name, G.active, G.initialization_date, G.termination_date,
                    G.periodicity, G.pickup_day, G.off_day, G.opening_time, G.closing_time,
                    G.email, G.address, G.observations, S.id AS society_id, S.name AS society_name,
                    S.cif_dni AS society_cif_dni, GT.id AS generator_type_id, GT.name AS generator_type_name,
                    GPN.phone_number
                    FROM generator G
                    JOIN society S ON G.society_id = S.id
                    JOIN generator_type GT ON G.generator_type_id = GT.id
                    JOIN generator_phone_number GPN ON G.id = GPN.generator_id
                    WHERE G.name LIKE '%GENERator 1%'"""
            ),
            arguments("FindAll with Relation filter",
                new GeneratorCriteria(null, "YYYYYYYYYY", null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null),
                List.of(
                    new GeneratorBuilder()
                        .withId(2).withName("Test Generator 2")
                        .withSociety(new Society(2,"YYYYYYYYYY","Test Society 2"))
                        .withActive(false).withAddress("C. Tulipán 2").build()
                ),
                """
                    SELECT G.id, G.name, G.active, G.initialization_date, G.termination_date,
                    G.periodicity, G.pickup_day, G.off_day, G.opening_time, G.closing_time,
                    G.email, G.address, G.observations, S.id AS society_id, S.name AS society_name,
                    S.cif_dni AS society_cif_dni, GT.id AS generator_type_id, GT.name AS generator_type_name,
                    GPN.phone_number
                    FROM generator G
                    JOIN society S ON G.society_id = S.id
                    JOIN generator_type GT ON G.generator_type_id = GT.id
                    JOIN generator_phone_number GPN ON G.id = GPN.generator_id
                    WHERE S.cif_dni = 'YYYYYYYYYY'"""
            ),
            arguments("FindAll with Equal filter",
                new GeneratorCriteria(null, null, null,
                    null, false, null, null,
                    null, null, null, null,
                    null, null, null, null),
                List.of(
                    new GeneratorBuilder()
                        .withId(2).withName("Test Generator 2")
                        .withSociety(new Society(2,"YYYYYYYYYY","Test Society 2"))
                        .withActive(false).withAddress("C. Tulipán 2").build()
                ),
                """
                    SELECT G.id, G.name, G.active, G.initialization_date, G.termination_date,
                    G.periodicity, G.pickup_day, G.off_day, G.opening_time, G.closing_time,
                    G.email, G.address, G.observations, S.id AS society_id, S.name AS society_name,
                    S.cif_dni AS society_cif_dni, GT.id AS generator_type_id, GT.name AS generator_type_name,
                    GPN.phone_number
                    FROM generator G
                    JOIN society S ON G.society_id = S.id
                    JOIN generator_type GT ON G.generator_type_id = GT.id
                    JOIN generator_phone_number GPN ON G.id = GPN.generator_id
                    WHERE G.active = FALSE"""
            ),
            arguments("FindAll with Date Range filter",
                new GeneratorCriteria(null, null, null,
                    null, null, LocalDate.of(2023, 6, 1), null,
                    null, null, null, null,
                    null, null, null, null),
                List.of(
                    new GeneratorBuilder()
                        .withId(3).withName("Test Generator 3")
                        .withGeneratorType(new GeneratorType(2, "Test GeneratorType 2"))
                        .withInitializationDate(LocalDate.of(2023, 6, 1))
                        .withAddress("C. Tulipán 3").withPhoneNumber(List.of("111111111")).build()
                ),
                """
                    SELECT G.id, G.name, G.active, G.initialization_date, G.termination_date,
                    G.periodicity, G.pickup_day, G.off_day, G.opening_time, G.closing_time,
                    G.email, G.address, G.observations, S.id AS society_id, S.name AS society_name,
                    S.cif_dni AS society_cif_dni, GT.id AS generator_type_id, GT.name AS generator_type_name,
                    GPN.phone_number
                    FROM generator G
                    JOIN society S ON G.society_id = S.id
                    JOIN generator_type GT ON G.generator_type_id = GT.id
                    JOIN generator_phone_number GPN ON G.id = GPN.generator_id
                    WHERE G.initialization_date BETWEEN '2023-6-1' AND CURRENT_DATE"""
            ),
            arguments("FindAll with Member filter",
                new GeneratorCriteria(null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, "111111111"),
                List.of(
                    new GeneratorBuilder().withId(1).withName("Test Generator 1")
                        .withPhoneNumber(List.of("111111111")).build(),
                    new GeneratorBuilder()
                        .withId(3).withName("Test Generator 3")
                        .withGeneratorType(new GeneratorType(2, "Test GeneratorType 2"))
                        .withInitializationDate(LocalDate.of(2023, 6, 1))
                        .withAddress("C. Tulipán 3").withPhoneNumber(List.of("111111111")).build()
                ),
                """
                    SELECT G.id, G.name, G.active, G.initialization_date, G.termination_date,
                    G.periodicity, G.pickup_day, G.off_day, G.opening_time, G.closing_time,
                    G.email, G.address, G.observations, S.id AS society_id, S.name AS society_name,
                    S.cif_dni AS society_cif_dni, GT.id AS generator_type_id, GT.name AS generator_type_name,
                    GPN.phone_number
                    FROM generator G
                    JOIN society S ON G.society_id = S.id
                    JOIN generator_type GT ON G.generator_type_id = GT.id
                    JOIN generator_phone_number GPN ON G.id = GPN.generator_id
                    WHERE GPN.phone_number = '111111111'"""
            ),
            arguments("FindAll with all filters",
                new GeneratorCriteria("Test Generator 3", "XXXXXXXXXX", "Test Society",
                    "Test GeneratorType 2", true, LocalDate.of(2023, 6, 1), null,
                    null, LocalDate.of(2023, 12, 31), 10, DayWeek.MONDAY.getDay(),
                    DayWeek.SUNDAY.getDay(), "test@gmail.com", "C. Tulipán", "111111111"),
                List.of(
                    new GeneratorBuilder()
                        .withId(3).withName("Test Generator 3")
                        .withGeneratorType(new GeneratorType(2, "Test GeneratorType 2"))
                        .withInitializationDate(LocalDate.of(2023, 6, 1))
                        .withAddress("C. Tulipán 3").withPhoneNumber(List.of("111111111")).build()
                ),
                """
                    SELECT G.id, G.name, G.active, G.initialization_date, G.termination_date,
                    G.periodicity, G.pickup_day, G.off_day, G.opening_time, G.closing_time,
                    G.email, G.address, G.observations, S.id AS society_id, S.name AS society_name,
                    S.cif_dni AS society_cif_dni, GT.id AS generator_type_id, GT.name AS generator_type_name,
                    GPN.phone_number
                    FROM generator G
                    JOIN society S ON G.society_id = S.id
                    JOIN generator_type GT ON G.generator_type_id = GT.id
                    JOIN generator_phone_number GPN ON G.id = GPN.generator_id
                    WHERE G.name = 'Test Generator 3' AND
                    S.cif_dni = 'XXXXXXXXXX' AND
                    S.name = 'Test Society' AND
                    GT.name = 'Test GeneratorType 2' AND
                    G.active = TRUE AND
                    G.initialization_date BETWEEN '2023-6-1' AND CURRENT_DATE AND
                    G.termination_date BETWEEN '-999999999-01-01' AND '2023-12-31' AND
                    G.periodicity = 10 AND
                    G.pickup_day = 'LUNES' AND
                    G.off_day = 'DOMINGO' AND
                    G.email = 'test@gmail.com' AND
                    G.address = 'C. Tulipán 3' AND
                    GPN.phone_number = '111111111'"""
            ),
            arguments("FindAll with unmatched filters",
                new GeneratorCriteria("Test Generator 4", "ZZZZZZZZZZ", null,
                    null, null, null, null,
                    null, null, null, DayWeek.THURSDAY.getDay(),
                    null, null, null, null),
                new ArrayList<>(),
                """
                    SELECT G.id, G.name, G.active, G.initialization_date, G.termination_date,
                    G.periodicity, G.pickup_day, G.off_day, G.opening_time, G.closing_time,
                    G.email, G.address, G.observations, S.id AS society_id, S.name AS society_name,
                    S.cif_dni AS society_cif_dni, GT.id AS generator_type_id, GT.name AS generator_type_name,
                    GPN.phone_number
                    FROM generator G
                    JOIN society S ON G.society_id = S.id
                    JOIN generator_type GT ON G.generator_type_id = GT.id
                    JOIN generator_phone_number GPN ON G.id = GPN.generator_id
                    WHERE G.name = 'Test Generator 4' AND
                    S.cif_dni = 'ZZZZZZZZZZ' AND
                    G.pickup_day = 'JUEVES'"""
            )
        );
    }

    @Test
    void testFindById() {
        societyServiceImpl.addSociety(new Society("XXXXXXXXXX", "Test Society"));
        generatorTypeServiceImpl.addGeneratorType(new GeneratorType("Test GeneratorType"));
        Generator storedGenerator = generatorServiceImpl.addGenerator(new GeneratorBuilder().build());

        Optional<Generator> resultGenerator = generatorServiceImpl.findById(storedGenerator.getId());

        Generator expectedGenerator = new GeneratorBuilder().withId(1).build();
        assertThat(resultGenerator)
            .isPresent()
            .contains((expectedGenerator));

        Optional<Generator> retrievedGenerator = generatorRepository.findById(resultGenerator.get().getId());
        assertThat(retrievedGenerator)
            .isPresent()
            .contains((expectedGenerator));
    }

    @Test
    void testAddGenerator() {
        societyServiceImpl.addSociety(new Society("XXXXXXXXXX", "Test Society"));
        generatorTypeServiceImpl.addGeneratorType(new GeneratorType("Test GeneratorType"));
        Generator generator = generatorServiceImpl.addGenerator(new GeneratorBuilder().build());

        Generator resultGenerator = generatorServiceImpl.addGenerator(generator);

        Generator expectedGenerator = new GeneratorBuilder().withId(1).build();
        assertThat(resultGenerator).isEqualTo(expectedGenerator);

        Optional<Generator> retrievedGenerator = generatorRepository.findById(resultGenerator.getId());
        assertThat(retrievedGenerator)
            .isPresent()
            .contains((expectedGenerator));
    }

    @Test
    void testUpdateGenerator() {
        societyServiceImpl.addSociety(new Society("XXXXXXXXXX", "Test Society"));
        generatorTypeServiceImpl.addGeneratorType(new GeneratorType("Test GeneratorType"));
        Generator storedGenerator = generatorServiceImpl.addGenerator(new GeneratorBuilder().build());

        Generator newGenerator = new GeneratorBuilder().withName("New test Generator").build();

        Generator resultGenerator = generatorServiceImpl.updateGenerator(storedGenerator.getId(), newGenerator);

        Generator expectedGenerator = new GeneratorBuilder().withId(1).withName("New test Generator").build();
        assertThat(resultGenerator).isEqualTo(expectedGenerator);

        Optional<Generator> retrievedGenerator = generatorRepository.findById(resultGenerator.getId());
        assertThat(retrievedGenerator)
            .isPresent()
            .contains((expectedGenerator));
    }

    @Test
    void testDeleteGenerator() {
        societyServiceImpl.addSociety(new Society("XXXXXXXXXX", "Test Society"));
        generatorTypeServiceImpl.addGeneratorType(new GeneratorType("Test GeneratorType"));
        Generator storedGenerator = generatorServiceImpl.addGenerator(new GeneratorBuilder().build());

        generatorServiceImpl.deleteGenerator(storedGenerator.getId());

        Optional<Generator> retrievedGenerator = generatorRepository.findById(storedGenerator.getId());
        assertThat(retrievedGenerator).isEmpty();
    }
}