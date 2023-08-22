package com.example.demo.unit.model.validation;

import com.example.demo.model.validation.ValueOfEnum;
import com.example.demo.model.validation.ValueOfEnumValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValueOfEnumValidatorUnitTest {

    private ValueOfEnumValidator validator;
    private ValueOfEnum annotation;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    public void setUp() {
        validator = new ValueOfEnumValidator();
        annotation = createAnnotation();
    }

    @Test
    void testIsValidWithValidValue() {
        validator.initialize(annotation);

        boolean isValid = validator.isValid("VALID", constraintValidatorContext);

        assertTrue(isValid);
    }

    @Test
    void testIsValidWithInvalidValue() {
        validator.initialize(annotation);

        boolean isValid = validator.isValid("INVALID", constraintValidatorContext);

        assertFalse(isValid);
    }

    @Test
    void testIsValidWithNullValue() {
        validator.initialize(annotation);

        boolean isValid = validator.isValid(null, constraintValidatorContext);

        assertTrue(isValid);
    }

    private ValueOfEnum createAnnotation() {
        return new ValueOfEnum() {
            @Override
            public Class<? extends Enum> enumClass() {
                return TestEnum.class;
            }

            @Override
            public String message() {
                return "Test message";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return ValueOfEnum.class;
            }
        };
    }

    private enum TestEnum {
        VALID,
        ANOTHER_VALID
    }
}