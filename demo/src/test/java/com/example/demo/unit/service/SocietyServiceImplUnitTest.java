package com.example.demo.unit.service;

import com.example.demo.exception.SocietyNotFoundException;
import com.example.demo.model.Society;
import com.example.demo.repository.SocietyRepository;
import com.example.demo.service.SocietyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

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
    void whenFindByIdSocietyDoesNotExist_thenShouldGiveOptionalEmpty() {
        when(societyRepository.findById((long) 1)).thenReturn(Optional.empty());

        Optional<Society> resultSociety = societyServiceImpl.findById(1);

        verify(societyRepository).findById((long) 1);
        assertThat(resultSociety)
            .isNotPresent();
    }

    @Test
    void testFindById() {
        Society expectedSociety = new Society(1, "cifDni", "name");

        when(societyRepository.findById((long) 1)).thenReturn(Optional.of(expectedSociety));

        Optional<Society> resultSociety = societyServiceImpl.findById(1);

        verify(societyRepository).findById((long) 1);
        assertThat(resultSociety)
            .isPresent()
            .contains((expectedSociety));
    }

    @Test
    void testAddSociety(){
        Society society = new Society("cifDni", "name");
        Society expectedSociety = new Society(1, "cifDni", "name");

        when(societyRepository.save(society)).thenReturn(expectedSociety);

        Society resultSociety = societyServiceImpl.addSociety(society);

        verify(societyRepository).save(society);
        assertThat(resultSociety).isEqualTo(expectedSociety);
    }

    @Test
    void whenUpdateSocietyDoesNotExist_thenShouldGiveSocietyNotFoundException() {
        Society newSociety = new Society("newCifDni", "newName");
        when(societyRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            societyServiceImpl.updateSociety(1, newSociety);
        }).isInstanceOf(SocietyNotFoundException.class)
            .hasMessageContaining("Society 1 not found");

        verify(societyRepository).findById((long) 1);
        verify(societyRepository, never()).save(any(Society.class));
    }

    @Test
    void testUpdateSociety() {
        Society newSociety = new Society("newCifDni", "newName");
        Society storedSociety = new Society(1, "cifDni", "name");
        Society expectedSociety = new Society(1, "newCifDni", "newName");
        when(societyRepository.findById((long) 1)).thenReturn(Optional.of(storedSociety));
        when(societyRepository.save(expectedSociety)).thenReturn(expectedSociety);

        Society resultSociety = societyServiceImpl.updateSociety(1, newSociety);

        verify(societyRepository).findById((long) 1);
        verify(societyRepository).save(expectedSociety);
        assertThat(resultSociety).isEqualTo(expectedSociety);
    }

    @Test
    void whenDeleteSocietyDoesNotExist_thenShouldGiveSocietyNotFoundException() {
        when(societyRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            societyServiceImpl.deleteSociety(1);
        }).isInstanceOf(SocietyNotFoundException.class)
            .hasMessageContaining("Society 1 not found");

        verify(societyRepository).findById((long) 1);
        verify(societyRepository, never()).delete(any(Society.class));
    }

    @Test
    void testDeleteSociety() {
        Society society = new Society(1, "cifDni", "name");
        when(societyRepository.findById((long) 1)).thenReturn(Optional.of(society));

        societyServiceImpl.deleteSociety(1);

        verify(societyRepository).findById((long) 1);
        verify(societyRepository).delete(society);
    }
}