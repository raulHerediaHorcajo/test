package com.example.demo.integration.society;

import com.example.demo.model.Society;
import com.example.demo.repository.SocietyRepository;
import com.example.demo.repository.criteria.SocietyCriteria;
import com.example.demo.service.SocietyServiceImpl;
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
class SocietyServiceImplIntegrationTest {

    @Autowired
    private SocietyServiceImpl societyServiceImpl;
    @Autowired
    private SocietyRepository societyRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void testFindAll(String scenario, SocietyCriteria filters, List<Society> expectedSocities, String retrieveSql) {
        List<Society> societies = List.of(
            new Society("XXXXXXXXXX","Test Society 1"),
            new Society("YYYYYYYYYY","Test Society 2"),
            new Society("ZZZZZZZZZZ","Test Society 3")
        );
        societyRepository.saveAll(societies);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Society> result = societyServiceImpl.findAll(filters, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isEqualTo(expectedSocities.size());
        assertThat(result.getContent())
            .containsAll(expectedSocities);
        assertThat(result.getPageable()).isEqualTo(pageable);

        List<Society> retrievedSocieties = jdbcTemplate.query(retrieveSql, JdbcTemplateMapperFactory
            .newInstance()
            .addKeys("id")
            .newRowMapper(Society.class));
        assertThat(retrievedSocieties).containsAll(expectedSocities);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("FindAll without filters",
                new SocietyCriteria(null, null),
                List.of(
                    new Society(1, "XXXXXXXXXX","Test Society 1"),
                    new Society(2, "YYYYYYYYYY","Test Society 2"),
                    new Society(3, "ZZZZZZZZZZ","Test Society 3")
                ),
                "SELECT * FROM society"
            ),
            arguments("FindAll with String filter",
                new SocietyCriteria(null, "Test Society 1"),
                List.of(
                    new Society(1, "XXXXXXXXXX","Test Society 1")
                ),
                "SELECT * FROM society WHERE name = 'Test Society 1'"
            ),
            arguments("FindAll with Partial String and Case Insensitivity filter",
                new SocietyCriteria("YYYyyy", null),
                List.of(
                    new Society(2, "YYYYYYYYYY","Test Society 2")
                ),
                "SELECT * FROM society WHERE cif_dni LIKE '%YYYyyy%'"
            ),
            arguments("FindAll with all filters",
                new SocietyCriteria("ZZZZZZZZZZ", "Test Society 3"),
                List.of(
                    new Society(3, "ZZZZZZZZZZ","Test Society 3")
                ),
                "SELECT * FROM society WHERE cif_dni = 'ZZZZZZZZZZ' AND name = 'Test Society 3'"
            ),
            arguments("FindAll with unmatched filters",
                new SocietyCriteria("XXXXXXXXXX", "Test Society 2"),
                new ArrayList<>(),
                "SELECT * FROM society WHERE cif_dni = 'XXXXXXXXXX' AND name = 'Test Society 2'"
            )
        );
    }

    @Test
    void testFindById() {
        Society storedSociety = societyServiceImpl.addSociety(new Society("XXXXXXXXXX", "Test Society"));

        Optional<Society> resultSociety = societyServiceImpl.findById(storedSociety.getId());

        Society expectedSociety = new Society(1, "XXXXXXXXXX", "Test Society");
        assertThat(resultSociety)
            .isPresent()
            .contains((expectedSociety));

        Optional<Society> retrievedSociety = societyRepository.findById(resultSociety.get().getId());
        assertThat(retrievedSociety)
            .isPresent()
            .contains((expectedSociety));
    }

    @Test
    void testAddSociety() {
        Society society = new Society("XXXXXXXXXX", "Test Society");

        Society resultSociety = societyServiceImpl.addSociety(society);

        Society expectedSociety = new Society(1, "XXXXXXXXXX", "Test Society");
        assertThat(resultSociety).isEqualTo(expectedSociety);

        Optional<Society> retrievedSociety = societyRepository.findById(resultSociety.getId());
        assertThat(retrievedSociety)
            .isPresent()
            .contains((expectedSociety));
    }

    @Test
    void testUpdateSociety() {
        Society storedSociety = societyServiceImpl.addSociety(new Society("XXXXXXXXXX", "Test Society"));

        Society newSociety = new Society("YYYYYYYYYY", "New test Society");

        Society resultSociety = societyServiceImpl.updateSociety(storedSociety.getId(), newSociety);

        Society expectedSociety = new Society(1, "YYYYYYYYYY", "New test Society");
        assertThat(resultSociety).isEqualTo(expectedSociety);

        Optional<Society> retrievedSociety = societyRepository.findById(resultSociety.getId());
        assertThat(retrievedSociety)
            .isPresent()
            .contains((expectedSociety));
    }

    @Test
    void testDeleteSociety() {
        Society storedSociety = societyServiceImpl.addSociety(new Society("XXXXXXXXXX", "Test Society"));

        societyServiceImpl.deleteSociety(storedSociety.getId());

        Optional<Society> retrievedSociety = societyRepository.findById(storedSociety.getId());
        assertThat(retrievedSociety).isEmpty();
    }
}