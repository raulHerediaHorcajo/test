package com.example.demo.unit.controller;

import com.example.demo.controller.GeneratorTypeRestController;
import com.example.demo.exception.GeneratorTypeNotFoundException;
import com.example.demo.model.GeneratorType;
import com.example.demo.repository.criteria.GeneratorTypeCriteria;
import com.example.demo.service.GeneratorTypeService;
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
class GeneratorTypeRestControllerUnitTest {

    private GeneratorTypeRestController generatorTypeRestController;

    @Mock
    private GeneratorTypeService generatorTypeService;

    @BeforeEach
    public void setUp() {
        generatorTypeRestController = new GeneratorTypeRestController(generatorTypeService);
    }

    @Test
    void testGetGeneratorTypes() {
        Pageable pageable = PageRequest.of(0, 20);
        List<GeneratorType> generatorTypes = List.of(mock(GeneratorType.class), mock(GeneratorType.class), mock(GeneratorType.class));
        Page<GeneratorType> page = new PageImpl<>(generatorTypes, pageable, 3);
        GeneratorTypeCriteria filters = mock(GeneratorTypeCriteria.class);
        when(generatorTypeService.findAll(filters, pageable)).thenReturn(page);

        ResponseEntity<Page<GeneratorType>> result = generatorTypeRestController.getGeneratorTypes(filters, pageable);

        verify(generatorTypeService).findAll(filters, pageable);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getNumberOfElements()).isEqualTo(3);
        assertThat(result.getBody().getContent()).containsAll(generatorTypes);
        assertThat(result.getBody().getPageable()).isEqualTo(pageable);
    }

    @Test
    void whenGetNotExistGeneratorTypeById_thenShouldGiveGeneratorTypeNotFoundException() {
        when(generatorTypeService.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generatorTypeRestController.getGeneratorType(1))
            .isInstanceOf(GeneratorTypeNotFoundException.class)
            .hasMessageContaining("GeneratorType 1 not found");

        verify(generatorTypeService).findById(1);
    }

    @Test
    void testGetGeneratorTypeById() {
        GeneratorType generatorType = mock(GeneratorType.class);
        when(generatorTypeService.findById(1)).thenReturn(Optional.of(generatorType));

        ResponseEntity<GeneratorType> result = generatorTypeRestController.getGeneratorType(1);

        verify(generatorTypeService).findById(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(generatorType);
    }


    @Test
    void testAddGeneratorType() {
        GeneratorType generatorType = mock(GeneratorType.class);
        when(generatorTypeService.addGeneratorType(generatorType)).thenReturn(generatorType);

        ResponseEntity<GeneratorType> result = generatorTypeRestController.addGeneratorType(generatorType);

        verify(generatorTypeService).addGeneratorType(generatorType);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(generatorType);
    }

    @Test
    void testUpdateGeneratorType() {
        GeneratorType generatorType = mock(GeneratorType.class);
        when(generatorTypeService.updateGeneratorType(1, generatorType)).thenReturn(generatorType);

        ResponseEntity<GeneratorType> result = generatorTypeRestController.updateGeneratorType(1, generatorType);

        verify(generatorTypeService).updateGeneratorType(1, generatorType);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(generatorType);
    }

    @Test
    void testDeleteGeneratorType() {
        ResponseEntity<Void> result = generatorTypeRestController.deleteGeneratorType(1);

        verify(generatorTypeService).deleteGeneratorType(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
