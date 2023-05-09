package com.example.demo.unit.exception;

import com.example.demo.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserNotFoundExceptionUnitTest {

    @Test
    void testUserNotFoundException(){
        assertThatThrownBy(() -> {
            throw new UserNotFoundException();
        }).isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    void testUserNotFoundExceptionWithEmail() {
        assertThatThrownBy(() -> {
            throw new UserNotFoundException("Test email");
        }).isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User with email Test email not found");
    }

    @Test
    void testUserNotFoundExceptionWithId() {
        assertThatThrownBy(() -> {
            throw new UserNotFoundException(1);
        }).isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User 1 not found");
    }
}
