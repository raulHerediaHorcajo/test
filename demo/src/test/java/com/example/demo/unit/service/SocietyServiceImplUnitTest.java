package com.example.demo.unit.service;

import com.example.demo.exception.SocietyNotFoundException;
import com.example.demo.model.Society;
import com.example.demo.repository.SocietyRepository;
import com.example.demo.repository.criteria.SocietyCriteria;
import com.example.demo.repository.specification.SocietySpecification;
import com.example.demo.service.SocietyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
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
    void whenFindAllWithUnmatchedFilters_thenShouldGiveEmptyPage() {
        SocietyCriteria filters = mock(SocietyCriteria.class);
        Specification<Society> specification = new SocietySpecification(filters);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Society> page = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(societyRepository.findAll(specification, pageable)).thenReturn(page);

        Page<Society> result = societyServiceImpl.findAll(filters, pageable);

        verify(societyRepository).findAll(specification, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isZero();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageable()).isEqualTo(pageable);
    }

    @Test
    void testFindAll() {
        SocietyCriteria filters = mock(SocietyCriteria.class);
        Specification<Society> specification = new SocietySpecification(filters);
        Pageable pageable = PageRequest.of(0, 20);
        List<Society> societies = List.of(
            new Society("XXXXXXXXXX","Test Society 1"),
            new Society("YYYYYYYYYY","Test Society 2"),
            new Society("ZZZZZZZZZZ","Test Society 3")
        );
        Page<Society> page = new PageImpl<>(societies, pageable, 3);

        when(societyRepository.findAll(specification, pageable)).thenReturn(page);

        Page<Society> result = societyServiceImpl.findAll(filters, pageable);

        verify(societyRepository).findAll(specification, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isEqualTo(3);
        assertThat(result.getContent()).containsAll(societies);
        assertThat(result.getPageable()).isEqualTo(pageable);
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