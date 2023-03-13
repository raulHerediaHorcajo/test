package com.example.demo.unit.exception;

import com.example.demo.exception.SocietyNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SocietyNotFoundExceptionUnitTest {

    @Test
    void testSocietyNotFoundException(){
        assertThatThrownBy(() -> {
            throw new SocietyNotFoundException();
        }).isInstanceOf(SocietyNotFoundException.class)
            .hasMessageContaining("Society not found");
    }

    @Test
    void testSocietyNotFoundExceptionWithMessage() {
        assertThatThrownBy(() -> {
            throw new SocietyNotFoundException("Test message");
        }).isInstanceOf(SocietyNotFoundException.class)
            .hasMessageContaining("Test message");
    }

    @Test
    void testSocietyNotFoundExceptionWithId() {
        assertThatThrownBy(() -> {
            throw new SocietyNotFoundException(1);
        }).isInstanceOf(SocietyNotFoundException.class)
            .hasMessageContaining("Society 1 not found");
    }
}
