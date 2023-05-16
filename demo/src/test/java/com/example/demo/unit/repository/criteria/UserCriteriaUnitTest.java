package com.example.demo.unit.repository.criteria;

import com.example.demo.repository.criteria.UserCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

class UserCriteriaUnitTest {

    private UserCriteria userCriteria;

    @BeforeEach
    public void setUp() {
        userCriteria = new UserCriteria("Test User",
            "test@gmail.com",
            List.of("ADMIN", "USER")
        );
    }

    @Test
    void testGetName() {
        assertEquals("Test User", userCriteria.getName());
    }

    @Test
    void testSetName() {
        userCriteria.setName("Name changed");
        assertEquals("Name changed", userCriteria.getName());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@gmail.com", userCriteria.getEmail());
    }

    @Test
    void testSetEmail() {
        userCriteria.setEmail("other@gmail.com");
        assertEquals("other@gmail.com", userCriteria.getEmail());
    }

    @Test
    void testGetRoles() {
        assertEquals(List.of("ADMIN", "USER"), userCriteria.getRoles());
    }

    @Test
    void testSetRoles() {
        userCriteria.setRoles(List.of("USER"));
        assertEquals(List.of("USER"), userCriteria.getRoles());
    }

    @Test
    void testEqualsAndHashCode() {
        UserCriteria duplicatedUserCriteria = new UserCriteria("Test User",
            "test@gmail.com",
            List.of("ADMIN", "USER"));
        assertThat(userCriteria.equals(duplicatedUserCriteria)).isTrue();
        assertEquals(userCriteria.hashCode(), duplicatedUserCriteria.hashCode());

        UserCriteria differentUserName = new UserCriteria("Distinct User",
            "test@gmail.com",
            List.of("ADMIN", "USER"));
        assertThat(userCriteria.equals(differentUserName)).isFalse();

        UserCriteria differentUserEmail = new UserCriteria("Test User",
            "other@gmail.com",
            List.of("ADMIN", "USER"));
        assertThat(userCriteria.equals(differentUserEmail)).isFalse();

        UserCriteria differentUserRoles = new UserCriteria("Test User",
            "test@gmail.com",
            List.of("USER"));
        assertThat(userCriteria.equals(differentUserEmail)).isFalse();

        UserCriteria distinctUserCriteria = new UserCriteria("Distinct User",
            "other@gmail.com",
            List.of("USER"));
        assertThat(userCriteria.equals(distinctUserCriteria)).isFalse();
        assertNotEquals(userCriteria.hashCode(), distinctUserCriteria.hashCode());

        assertThat(userCriteria.equals(userCriteria)).isTrue();
        assertThat(userCriteria.equals(null)).isFalse();
        assertThat(userCriteria.equals(new Object())).isFalse();
        assertThat(userCriteria.equals(mock(UserCriteria.class))).isFalse();
    }
}
