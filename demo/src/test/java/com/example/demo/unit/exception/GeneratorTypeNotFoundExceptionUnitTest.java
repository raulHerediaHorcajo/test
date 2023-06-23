package com.example.demo.unit.exception;

import com.example.demo.exception.GeneratorTypeNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class GeneratorTypeNotFoundExceptionUnitTest {

    @Test
    void testGeneratorTypeNotFoundException(){
        assertThatThrownBy(() -> {
            throw new GeneratorTypeNotFoundException();
        }).isInstanceOf(GeneratorTypeNotFoundException.class)
            .hasMessageContaining("GeneratorType not found");
    }

    @Test
    void testGeneratorTypeNotFoundExceptionWithName() {
        assertThatThrownBy(() -> {
            throw new GeneratorTypeNotFoundException("Test name");
        }).isInstanceOf(GeneratorTypeNotFoundException.class)
            .hasMessageContaining("GeneratorType Test name not found");
    }

    @Test
    void testGeneratorTypeNotFoundExceptionWithId() {
        assertThatThrownBy(() -> {
            throw new GeneratorTypeNotFoundException(1);
        }).isInstanceOf(GeneratorTypeNotFoundException.class)
            .hasMessageContaining("GeneratorType 1 not found");
    }
}
