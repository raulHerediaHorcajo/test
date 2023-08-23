package com.example.demo.unit.exception;

import com.example.demo.exception.GeneratorNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class GeneratorNotFoundExceptionUnitTest {

    @Test
    void testGeneratorNotFoundException(){
        assertThatThrownBy(() -> {
            throw new GeneratorNotFoundException();
        }).isInstanceOf(GeneratorNotFoundException.class)
            .hasMessageContaining("Generator not found");
    }

    @Test
    void testGeneratorNotFoundExceptionWithMessage() {
        assertThatThrownBy(() -> {
            throw new GeneratorNotFoundException("Test message");
        }).isInstanceOf(GeneratorNotFoundException.class)
            .hasMessageContaining("Test message");
    }

    @Test
    void testGeneratorNotFoundExceptionWithId() {
        assertThatThrownBy(() -> {
            throw new GeneratorNotFoundException(1);
        }).isInstanceOf(GeneratorNotFoundException.class)
            .hasMessageContaining("Generator 1 not found");
    }
}