package org.juwont.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = DecimalPlacesValidator.class)
@Documented
public @interface DecimalPlaces {

    int decimalPlaces() default 2;

    String message() default "decimal places must not be more than {decimalPlaces}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}