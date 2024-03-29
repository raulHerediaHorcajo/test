package com.example.demo.integration.generatortype;

import com.example.demo.model.GeneratorType;
import com.example.demo.repository.GeneratorTypeRepository;
import com.example.demo.repository.criteria.GeneratorTypeCriteria;
import com.example.demo.service.GeneratorTypeServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class GeneratorTypeServiceImplIntegrationTest {

    @Autowired
    private GeneratorTypeServiceImpl generatorTypeServiceImpl;
    @Autowired
    private GeneratorTypeRepository generatorTypeRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void testFindAll(String scenario, GeneratorTypeCriteria filters, List<GeneratorType> expectedGeneratorTypes, String retrieveSql) {
        List<GeneratorType> generatorTypes = List.of(
            new GeneratorType("Test GeneratorType 1"),
            new GeneratorType("Test GeneratorType 2"),
            new GeneratorType("Test GeneratorType 3")
        );
        generatorTypeRepository.saveAll(generatorTypes);

        Pageable pageable = PageRequest.of(0, 20);
        Page<GeneratorType> result = generatorTypeServiceImpl.findAll(filters, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isEqualTo(expectedGeneratorTypes.size());
        assertThat(result.getContent())
            .containsAll(expectedGeneratorTypes);
        assertThat(result.getPageable()).isEqualTo(pageable);

        List<GeneratorType> retrievedGeneratorTypes = jdbcTemplate.query(retrieveSql, JdbcTemplateMapperFactory
            .newInstance()
            .addKeys("id")
            .newRowMapper(GeneratorType.class));
        assertThat(retrievedGeneratorTypes).containsAll(expectedGeneratorTypes);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("FindAll without filters",
                new GeneratorTypeCriteria(null),
                List.of(
                    new GeneratorType(1, "Test GeneratorType 1"),
                    new GeneratorType(2, "Test GeneratorType 2"),
                    new GeneratorType(3, "Test GeneratorType 3")
                ),
                "SELECT * FROM generator_type"
            ),
            arguments("FindAll with String filter",
                new GeneratorTypeCriteria("Test GeneratorType 1"),
                List.of(
                    new GeneratorType(1, "Test GeneratorType 1")
                ),
                "SELECT * FROM generator_type WHERE name = 'Test GeneratorType 1'"
            ),
            arguments("FindAll with Partial String and Case Insensitivity filter",
                new GeneratorTypeCriteria("GENERATORType 2"),
                List.of(
                    new GeneratorType(2, "Test GeneratorType 2")
                ),
                "SELECT * FROM generator_type WHERE name LIKE '%GENERATORType 2%'"
            ),
            arguments("FindAll with all filters",
                new GeneratorTypeCriteria("Test GeneratorType 3"),
                List.of(
                    new GeneratorType(3, "Test GeneratorType 3")
                ),
                "SELECT * FROM generator_type WHERE name = 'Test GeneratorType 3'"
            ),
            arguments("FindAll with unmatched filters",
                new GeneratorTypeCriteria("Test GeneratorType 4"),
                new ArrayList<>(),
                "SELECT * FROM generator_type WHERE name = 'Test GeneratorType 4'"
            )
        );
    }

    @Test
    void testFindById() {
        GeneratorType storedGeneratorType = generatorTypeServiceImpl.addGeneratorType(new GeneratorType("Test GeneratorType"));

        Optional<GeneratorType> resultGeneratorType = generatorTypeServiceImpl.findById(storedGeneratorType.getId());

        GeneratorType expectedGeneratorType = new GeneratorType(1, "Test GeneratorType");
        assertThat(resultGeneratorType)
            .isPresent()
            .contains((expectedGeneratorType));

        Optional<GeneratorType> retrievedGeneratorType = generatorTypeRepository.findById(resultGeneratorType.get().getId());
        assertThat(retrievedGeneratorType)
            .isPresent()
            .contains((expectedGeneratorType));
    }

    @Test
    void testAddGeneratorType() {
        GeneratorType generatorType = new GeneratorType("Test GeneratorType");

        GeneratorType resultGeneratorType = generatorTypeServiceImpl.addGeneratorType(generatorType);

        GeneratorType expectedGeneratorType = new GeneratorType(1, "Test GeneratorType");
        assertThat(resultGeneratorType).isEqualTo(expectedGeneratorType);

        Optional<GeneratorType> retrievedGeneratorType = generatorTypeRepository.findById(resultGeneratorType.getId());
        assertThat(retrievedGeneratorType)
            .isPresent()
            .contains((expectedGeneratorType));
    }

    @Test
    void testUpdateGeneratorType() {
        GeneratorType storedGeneratorType = generatorTypeServiceImpl.addGeneratorType(new GeneratorType("Test GeneratorType"));

        GeneratorType newGeneratorType = new GeneratorType("New test GeneratorType");

        GeneratorType resultGeneratorType = generatorTypeServiceImpl.updateGeneratorType(storedGeneratorType.getId(), newGeneratorType);

        GeneratorType expectedGeneratorType = new GeneratorType(1, "New test GeneratorType");
        assertThat(resultGeneratorType).isEqualTo(expectedGeneratorType);

        Optional<GeneratorType> retrievedGeneratorType = generatorTypeRepository.findById(resultGeneratorType.getId());
        assertThat(retrievedGeneratorType)
            .isPresent()
            .contains((expectedGeneratorType));
    }

    @Test
    void testDeleteGeneratorType() {
        GeneratorType storedGeneratorType = generatorTypeServiceImpl.addGeneratorType(new GeneratorType("Test GeneratorType"));

        generatorTypeServiceImpl.deleteGeneratorType(storedGeneratorType.getId());

        Optional<GeneratorType> retrievedGeneratorType = generatorTypeRepository.findById(storedGeneratorType.getId());
        assertThat(retrievedGeneratorType).isEmpty();
    }
}