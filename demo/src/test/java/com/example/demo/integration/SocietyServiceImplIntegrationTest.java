package com.example.demo.integration;

import com.example.demo.model.Society;
import com.example.demo.repository.SocietyRepository;
import com.example.demo.service.SocietyServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SocietyServiceImplIntegrationTest {

    @Autowired
    private SocietyServiceImpl societyServiceImpl;

    @Autowired
    private SocietyRepository societyRepository;

    @Test
    void testFindById() {
        societyRepository.save(new Society(1, "cifDni", "name"));

        Optional<Society> resultSociety = societyServiceImpl.findById(1);

        Society expectedSociety = new Society(1, "cifDni", "name");
        assertThat(resultSociety)
            .isPresent()
            .contains((expectedSociety));

        Optional<Society> retrievedSociety = societyRepository.findById((long) 1);
        assertThat(retrievedSociety)
            .isPresent()
            .contains((expectedSociety));
    }

    @Test
    void testAddSociety() {
        Society society = new Society("cifDni", "name");

        Society resultSociety = societyServiceImpl.addSociety(society);

        Society expectedSociety = new Society(1, "cifDni", "name");
        assertThat(resultSociety).isEqualTo(expectedSociety);

        Optional<Society> retrievedSociety = societyRepository.findById(resultSociety.getId());
        assertThat(retrievedSociety)
            .isPresent()
            .contains((expectedSociety));
    }
}