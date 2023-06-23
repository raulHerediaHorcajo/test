package com.example.demo.unit.repository.criteria;

import com.example.demo.repository.criteria.GeneratorTypeCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

class GeneratorTypeCriteriaUnitTest {

    private GeneratorTypeCriteria generatorTypeCriteria;

    @BeforeEach
    public void setUp() {
        generatorTypeCriteria = new GeneratorTypeCriteria("Test GeneratorType");
    }

    @Test
    void testGetName() {
        assertEquals("Test GeneratorType", generatorTypeCriteria.getName());
    }

    @Test
    void testSetName() {
        generatorTypeCriteria.setName("Name changed");
        assertEquals("Name changed", generatorTypeCriteria.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        GeneratorTypeCriteria duplicatedGeneratorTypeCriteria = new GeneratorTypeCriteria("Test GeneratorType");
        assertThat(generatorTypeCriteria.equals(duplicatedGeneratorTypeCriteria)).isTrue();
        assertEquals(generatorTypeCriteria.hashCode(), duplicatedGeneratorTypeCriteria.hashCode());

        GeneratorTypeCriteria distinctGeneratorTypeCriteria = new GeneratorTypeCriteria("Distinct GeneratorType");
        assertThat(generatorTypeCriteria.equals(distinctGeneratorTypeCriteria)).isFalse();
        assertNotEquals(generatorTypeCriteria.hashCode(), distinctGeneratorTypeCriteria.hashCode());

        assertThat(generatorTypeCriteria.equals(generatorTypeCriteria)).isTrue();
        assertThat(generatorTypeCriteria.equals(null)).isFalse();
        assertThat(generatorTypeCriteria.equals(new Object())).isFalse();
        assertThat(generatorTypeCriteria.equals(mock(GeneratorTypeCriteria.class))).isFalse();
    }
}
