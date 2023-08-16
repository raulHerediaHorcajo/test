package com.example.demo.unit.service;

import com.example.demo.exception.GeneratorTypeNotFoundException;
import com.example.demo.model.GeneratorType;
import com.example.demo.repository.GeneratorTypeRepository;
import com.example.demo.repository.criteria.GeneratorTypeCriteria;
import com.example.demo.repository.specification.GeneratorTypeSpecification;
import com.example.demo.service.GeneratorTypeServiceImpl;
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
class GeneratorTypeServiceImplUnitTest {

    private GeneratorTypeServiceImpl generatorTypeServiceImpl;

    @Mock
    private GeneratorTypeRepository generatorTypeRepository;

    @BeforeEach
    public void setUp() {
        generatorTypeServiceImpl = new GeneratorTypeServiceImpl(generatorTypeRepository);
    }

    @Test
    void whenFindAllWithUnmatchedFilters_thenShouldGiveEmptyPage() {
        GeneratorTypeCriteria filters = mock(GeneratorTypeCriteria.class);
        Specification<GeneratorType> specification = new GeneratorTypeSpecification(filters);
        Pageable pageable = PageRequest.of(0, 20);
        Page<GeneratorType> page = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(generatorTypeRepository.findAll(specification, pageable)).thenReturn(page);

        Page<GeneratorType> result = generatorTypeServiceImpl.findAll(filters, pageable);

        verify(generatorTypeRepository).findAll(specification, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isZero();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageable()).isEqualTo(pageable);
    }

    @Test
    void testFindAll() {
        GeneratorTypeCriteria filters = mock(GeneratorTypeCriteria.class);
        Specification<GeneratorType> specification = new GeneratorTypeSpecification(filters);
        Pageable pageable = PageRequest.of(0, 20);
        List<GeneratorType> generatorTypes = List.of(
            new GeneratorType("Test GeneratorType 1"),
            new GeneratorType("Test GeneratorType 2"),
            new GeneratorType("Test GeneratorType 3")
        );
        Page<GeneratorType> page = new PageImpl<>(generatorTypes, pageable, 3);

        when(generatorTypeRepository.findAll(specification, pageable)).thenReturn(page);

        Page<GeneratorType> result = generatorTypeServiceImpl.findAll(filters, pageable);

        verify(generatorTypeRepository).findAll(specification, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isEqualTo(3);
        assertThat(result.getContent()).containsAll(generatorTypes);
        assertThat(result.getPageable()).isEqualTo(pageable);
    }

    @Test
    void whenFindByIdGeneratorTypeDoesNotExist_thenShouldGiveOptionalEmpty() {
        when(generatorTypeRepository.findById((long) 1)).thenReturn(Optional.empty());

        Optional<GeneratorType> resultGeneratorType = generatorTypeServiceImpl.findById(1);

        verify(generatorTypeRepository).findById((long) 1);
        assertThat(resultGeneratorType)
            .isNotPresent();
    }

    @Test
    void testFindById() {
        GeneratorType expectedGeneratorType = new GeneratorType(1, "Test GeneratorType");

        when(generatorTypeRepository.findById((long) 1)).thenReturn(Optional.of(expectedGeneratorType));

        Optional<GeneratorType> resultGeneratorType = generatorTypeServiceImpl.findById(1);

        verify(generatorTypeRepository).findById((long) 1);
        assertThat(resultGeneratorType)
            .isPresent()
            .contains((expectedGeneratorType));
    }

    @Test
    void testAddGeneratorType(){
        GeneratorType generatorType = new GeneratorType("Test GeneratorType");
        GeneratorType expectedGeneratorType = new GeneratorType(1, "Test GeneratorType");

        when(generatorTypeRepository.save(generatorType)).thenReturn(expectedGeneratorType);

        GeneratorType resultGeneratorType = generatorTypeServiceImpl.addGeneratorType(generatorType);

        verify(generatorTypeRepository).save(generatorType);
        assertThat(resultGeneratorType).isEqualTo(expectedGeneratorType);
    }

    @Test
    void whenUpdateGeneratorTypeDoesNotExist_thenShouldGiveGeneratorTypeNotFoundException() {
        GeneratorType newGeneratorType = new GeneratorType("New test GeneratorType");
        when(generatorTypeRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generatorTypeServiceImpl.updateGeneratorType(1, newGeneratorType))
            .isInstanceOf(GeneratorTypeNotFoundException.class)
            .hasMessageContaining("GeneratorType 1 not found");

        verify(generatorTypeRepository).findById((long) 1);
        verify(generatorTypeRepository, never()).save(any(GeneratorType.class));
    }

    @Test
    void testUpdateGeneratorType() {
        GeneratorType newGeneratorType = new GeneratorType("New test GeneratorType");
        GeneratorType storedGeneratorType = new GeneratorType(1, "Test GeneratorType");
        GeneratorType expectedGeneratorType = new GeneratorType(1, "New test GeneratorType");
        when(generatorTypeRepository.findById((long) 1)).thenReturn(Optional.of(storedGeneratorType));
        when(generatorTypeRepository.save(newGeneratorType)).thenReturn(expectedGeneratorType);

        GeneratorType resultGeneratorType = generatorTypeServiceImpl.updateGeneratorType(1, newGeneratorType);

        verify(generatorTypeRepository).findById((long) 1);
        verify(generatorTypeRepository).save(newGeneratorType);
        assertThat(resultGeneratorType).isEqualTo(expectedGeneratorType);
    }

    @Test
    void whenDeleteGeneratorTypeDoesNotExist_thenShouldGiveGeneratorTypeNotFoundException() {
        when(generatorTypeRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generatorTypeServiceImpl.deleteGeneratorType(1))
            .isInstanceOf(GeneratorTypeNotFoundException.class)
            .hasMessageContaining("GeneratorType 1 not found");

        verify(generatorTypeRepository).findById((long) 1);
        verify(generatorTypeRepository, never()).delete(any(GeneratorType.class));
    }

    @Test
    void testDeleteGeneratorType() {
        GeneratorType generatorType = new GeneratorType(1, "Test GeneratorType");
        when(generatorTypeRepository.findById((long) 1)).thenReturn(Optional.of(generatorType));

        generatorTypeServiceImpl.deleteGeneratorType(1);

        verify(generatorTypeRepository).findById((long) 1);
        verify(generatorTypeRepository).delete(generatorType);
    }
}