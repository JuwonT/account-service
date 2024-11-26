package org.juwont.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class DecimalPlacesValidator implements ConstraintValidator<DecimalPlaces, BigDecimal> {

    private int decimalPlaces;

    @Override
    public void initialize(final DecimalPlaces constraintAnnotation) {
        decimalPlaces = constraintAnnotation.decimalPlaces();
    }

    @Override
    public boolean isValid(final BigDecimal value, final ConstraintValidatorContext context) {
        return value == null || areActualDecimalPlacesLessThanOrEqualToExpected(value);
    }

    private boolean areActualDecimalPlacesLessThanOrEqualToExpected(final BigDecimal value) {
        return value.stripTrailingZeros().scale() <= decimalPlaces;
    }
}
