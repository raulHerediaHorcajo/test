package com.example.demo.integration;

import com.example.demo.model.Society;
import com.example.demo.repository.SocietyRepository;
import com.example.demo.service.SocietyServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SocietyServiceImplIntegrationTest {

    @Autowired
    private SocietyServiceImpl societyServiceImpl;

    @Autowired
    private SocietyRepository societyRepository;

    @Test
    void testAddSociety() {
        Society society = new Society("cifDni", "name");

        Society resultSociety = societyServiceImpl.addSociety(society);

        Society expectedSociety = new Society(1, "cifDni", "name");
        assertThat(resultSociety).isEqualTo(expectedSociety);

        Society retrievedSociety = societyRepository.findById((long) 1).orElse(null);
        assertThat(retrievedSociety)
            .isNotNull()
            .isEqualTo(expectedSociety);
    }
}