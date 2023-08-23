package com.example.demo.unit.controller;

import com.example.demo.controller.GeneratorRestController;
import com.example.demo.exception.GeneratorNotFoundException;
import com.example.demo.model.Generator;
import com.example.demo.repository.criteria.GeneratorCriteria;
import com.example.demo.service.GeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneratorRestControllerUnitTest {

    private GeneratorRestController generatorRestController;

    @Mock
    private GeneratorService generatorService;

    @BeforeEach
    public void setUp() {
        generatorRestController = new GeneratorRestController(generatorService);
    }

    @Test
    void testGetGenerators() {
        Pageable pageable = PageRequest.of(0, 20);
        List<Generator> generators = List.of(mock(Generator.class), mock(Generator.class), mock(Generator.class));
        Page<Generator> page = new PageImpl<>(generators, pageable, 3);
        GeneratorCriteria filters = mock(GeneratorCriteria.class);
        when(generatorService.findAll(filters, pageable)).thenReturn(page);

        ResponseEntity<Page<Generator>> result = generatorRestController.getGenerators(filters, pageable);

        verify(generatorService).findAll(filters, pageable);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getNumberOfElements()).isEqualTo(3);
        assertThat(result.getBody().getContent()).containsAll(generators);
        assertThat(result.getBody().getPageable()).isEqualTo(pageable);
    }

    @Test
    void whenGetNotExistGeneratorById_thenShouldGiveGeneratorNotFoundException() {
        when(generatorService.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generatorRestController.getGenerator(1))
            .isInstanceOf(GeneratorNotFoundException.class)
            .hasMessageContaining("Generator 1 not found");

        verify(generatorService).findById(1);
    }

    @Test
    void testGetGeneratorById() {
        Generator generator = mock(Generator.class);
        when(generatorService.findById(1)).thenReturn(Optional.of(generator));

        ResponseEntity<Generator> result = generatorRestController.getGenerator(1);

        verify(generatorService).findById(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(generator);
    }


    @Test
    void testAddGenerator() {
        Generator generator = mock(Generator.class);
        when(generatorService.addGenerator(generator)).thenReturn(generator);

        ResponseEntity<Generator> result = generatorRestController.addGenerator(generator);

        verify(generatorService).addGenerator(generator);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(generator);
    }

    @Test
    void testUpdateGenerator() {
        Generator generator = mock(Generator.class);
        when(generatorService.updateGenerator(1, generator)).thenReturn(generator);

        ResponseEntity<Generator> result = generatorRestController.updateGenerator(1, generator);

        verify(generatorService).updateGenerator(1, generator);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(generator);
    }

    @Test
    void testDeleteGenerator() {
        ResponseEntity<Void> result = generatorRestController.deleteGenerator(1);

        verify(generatorService).deleteGenerator(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}