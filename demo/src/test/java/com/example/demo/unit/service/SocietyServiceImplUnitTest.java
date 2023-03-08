package com.example.demo.unit.service;

import com.example.demo.model.Society;
import com.example.demo.repository.SocietyRepository;
import com.example.demo.service.SocietyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SocietyServiceImplUnitTest {

    private SocietyServiceImpl societyServiceImpl;

    @Mock
    private SocietyRepository societyRepository;

    @BeforeEach
    public void setUp() {
        societyServiceImpl = new SocietyServiceImpl(societyRepository);
    }

    @Test
    void testAddSociety() {
        Society society = new Society("cifDni", "name");
        Society expectedSociety = new Society(1, "cifDni", "name");

        when(societyRepository.save(society)).thenReturn(expectedSociety);

        Society resultSociety = societyServiceImpl.addSociety(society);

        verify(societyRepository).save(society);
        assertThat(resultSociety).isEqualTo(expectedSociety);
    }
}