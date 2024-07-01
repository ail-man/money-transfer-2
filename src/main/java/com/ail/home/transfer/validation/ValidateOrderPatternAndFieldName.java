package com.ail.home.transfer.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

/**
 * Validation annotation @ValidateOrderPatternAndFieldName used for validating pattern and field name from 'order' field from the Criteria
 * classes.
 * This annotation first validates the pattern of the 'order' content.
 * Afterwards, it validates if the field in 'order' is a property in the entityClass.
 */
@Target({ FIELD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldNameValidator.class)
@Pattern(regexp = "^[+-][a-zA-Z_$][a-zA-Z_$0-9]*$")
@ReportAsSingleViolation
public @interface ValidateOrderPatternAndFieldName {

	String message() default "must match \"^[+-][a-zA-Z_$][a-zA-Z_$0-9]*$\"";

	Class<?> entityClass();

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
