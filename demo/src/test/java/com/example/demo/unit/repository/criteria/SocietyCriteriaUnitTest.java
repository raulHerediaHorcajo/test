package com.example.demo.unit.repository.criteria;

import com.example.demo.repository.criteria.SocietyCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

class SocietyCriteriaUnitTest {

    private SocietyCriteria societyCriteria;

    @BeforeEach
    public void setUp() {
        societyCriteria = new SocietyCriteria("XXXXXXXXXX", "Test Society");
    }

    @Test
    void testGetCifDni() {
        assertEquals("XXXXXXXXXX", societyCriteria.getCifDni());
    }

    @Test
    void testSetCifDni() {
        societyCriteria.setCifDni("YYYYYYYYYY");
        assertEquals("YYYYYYYYYY", societyCriteria.getCifDni());
    }

    @Test
    void testGetName() {
        assertEquals("Test Society", societyCriteria.getName());
    }

    @Test
    void testSetName() {
        societyCriteria.setName("Name changed");
        assertEquals("Name changed", societyCriteria.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        SocietyCriteria duplicatedSocietyCriteria = new SocietyCriteria("XXXXXXXXXX", "Test Society");
        assertThat(societyCriteria.equals(duplicatedSocietyCriteria)).isTrue();
        assertEquals(societyCriteria.hashCode(), duplicatedSocietyCriteria.hashCode());

        /*Society differentSocietyName = new Society(1, "XXXXXXXXXX", "Distinct Society");
        assertThat(society.equals(differentSocietyName)).isFalse();*/

        SocietyCriteria distinctSocietyCriteria = new SocietyCriteria("YYYYYYYYYY", "Distinct Society");
        assertThat(societyCriteria.equals(distinctSocietyCriteria)).isFalse();
        assertNotEquals(societyCriteria.hashCode(), distinctSocietyCriteria.hashCode());

        assertThat(societyCriteria.equals(societyCriteria)).isTrue();
        assertThat(societyCriteria.equals(null)).isFalse();
        assertThat(societyCriteria.equals(new Object())).isFalse();
        assertThat(societyCriteria.equals(mock(SocietyCriteria.class))).isFalse();
    }
}
