package com.example.demo.unit.service;

import com.example.demo.exception.GeneratorNotFoundException;
import com.example.demo.model.Generator;
import com.example.demo.repository.GeneratorRepository;
import com.example.demo.repository.criteria.GeneratorCriteria;
import com.example.demo.repository.specification.GeneratorSpecification;
import com.example.demo.service.GeneratorServiceImpl;
import com.example.demo.util.builder.GeneratorBuilder;
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
class GeneratorServiceImplUnitTest {

    private GeneratorServiceImpl generatorServiceImpl;

    @Mock
    private GeneratorRepository generatorRepository;

    @BeforeEach
    public void setUp() {
        generatorServiceImpl = new GeneratorServiceImpl(generatorRepository);
    }

    @Test
    void whenFindAllWithUnmatchedFilters_thenShouldGiveEmptyPage() {
        GeneratorCriteria filters = mock(GeneratorCriteria.class);
        Specification<Generator> specification = new GeneratorSpecification(filters);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Generator> page = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(generatorRepository.findAll(specification, pageable)).thenReturn(page);

        Page<Generator> result = generatorServiceImpl.findAll(filters, pageable);

        verify(generatorRepository).findAll(specification, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isZero();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageable()).isEqualTo(pageable);
    }

    @Test
    void testFindAll() {
        GeneratorCriteria filters = mock(GeneratorCriteria.class);
        Specification<Generator> specification = new GeneratorSpecification(filters);
        Pageable pageable = PageRequest.of(0, 20);
        List<Generator> generators = List.of(
            new GeneratorBuilder().withName("Test Generator 1").build(),
            new GeneratorBuilder().withName("Test Generator 2").build(),
            new GeneratorBuilder().withName("Test Generator 3").build()
        );
        Page<Generator> page = new PageImpl<>(generators, pageable, 3);

        when(generatorRepository.findAll(specification, pageable)).thenReturn(page);

        Page<Generator> result = generatorServiceImpl.findAll(filters, pageable);

        verify(generatorRepository).findAll(specification, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isEqualTo(3);
        assertThat(result.getContent()).containsAll(generators);
        assertThat(result.getPageable()).isEqualTo(pageable);
    }

    @Test
    void whenFindByIdGeneratorDoesNotExist_thenShouldGiveOptionalEmpty() {
        when(generatorRepository.findById((long) 1)).thenReturn(Optional.empty());

        Optional<Generator> resultGenerator = generatorServiceImpl.findById(1);

        verify(generatorRepository).findById((long) 1);
        assertThat(resultGenerator)
            .isNotPresent();
    }

    @Test
    void testFindById() {
        Generator expectedGenerator = new GeneratorBuilder().withId(1).build();

        when(generatorRepository.findById((long) 1)).thenReturn(Optional.of(expectedGenerator));

        Optional<Generator> resultGenerator = generatorServiceImpl.findById(1);

        verify(generatorRepository).findById((long) 1);
        assertThat(resultGenerator)
            .isPresent()
            .contains((expectedGenerator));
    }

    @Test
    void testAddGenerator(){
        Generator generator = new GeneratorBuilder().build();
        Generator expectedGenerator = new GeneratorBuilder().withId(1).build();

        when(generatorRepository.save(generator)).thenReturn(expectedGenerator);

        Generator resultGenerator = generatorServiceImpl.addGenerator(generator);

        verify(generatorRepository).save(generator);
        assertThat(resultGenerator).isEqualTo(expectedGenerator);
    }

    @Test
    void whenUpdateGeneratorDoesNotExist_thenShouldGiveGeneratorNotFoundException() {
        Generator newGenerator = new GeneratorBuilder().build();
        when(generatorRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generatorServiceImpl.updateGenerator(1, newGenerator))
            .isInstanceOf(GeneratorNotFoundException.class)
            .hasMessageContaining("Generator 1 not found");

        verify(generatorRepository).findById((long) 1);
        verify(generatorRepository, never()).save(any(Generator.class));
    }

    @Test
    void testUpdateGenerator() {
        Generator newGenerator = new GeneratorBuilder().withName("New test Generator").build();
        Generator storedGenerator = new GeneratorBuilder().withId(1).build();
        Generator expectedGenerator = new GeneratorBuilder().withId(1).withName("New test Generator").build();
        when(generatorRepository.findById((long) 1)).thenReturn(Optional.of(storedGenerator));
        when(generatorRepository.save(newGenerator)).thenReturn(expectedGenerator);

        Generator resultGenerator = generatorServiceImpl.updateGenerator(1, newGenerator);

        verify(generatorRepository).findById((long) 1);
        verify(generatorRepository).save(newGenerator);
        assertThat(resultGenerator).isEqualTo(expectedGenerator);
    }

    @Test
    void whenDeleteGeneratorDoesNotExist_thenShouldGiveGeneratorNotFoundException() {
        when(generatorRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generatorServiceImpl.deleteGenerator(1))
            .isInstanceOf(GeneratorNotFoundException.class)
            .hasMessageContaining("Generator 1 not found");

        verify(generatorRepository).findById((long) 1);
        verify(generatorRepository, never()).delete(any(Generator.class));
    }

    @Test
    void testDeleteGenerator() {
        Generator generator = new GeneratorBuilder().withId(1).build();
        when(generatorRepository.findById((long) 1)).thenReturn(Optional.of(generator));

        generatorServiceImpl.deleteGenerator(1);

        verify(generatorRepository).findById((long) 1);
        verify(generatorRepository).delete(generator);
    }
}