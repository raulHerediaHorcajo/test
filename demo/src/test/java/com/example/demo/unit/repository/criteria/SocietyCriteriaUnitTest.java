package com.example.demo.unit.repository.criteria;

import com.example.demo.repository.criteria.SocietyCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
