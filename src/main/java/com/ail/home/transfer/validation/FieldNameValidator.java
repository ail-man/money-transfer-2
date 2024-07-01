package com.ail.home.transfer.validation;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Configuration
public class FieldNameValidator implements ConstraintValidator<ValidateOrderPatternAndFieldName, String> {

	@Override
	public boolean isValid(final String field, final ConstraintValidatorContext context) {
		final ConstraintValidatorContextImpl contextImpl = (ConstraintValidatorContextImpl) context;
		final ConstraintDescriptorImpl<ValidateOrderPatternAndFieldName> descriptor =
			(ConstraintDescriptorImpl<ValidateOrderPatternAndFieldName>) contextImpl.getConstraintDescriptor();
		final ValidateOrderPatternAndFieldName annotation = descriptor.getAnnotationDescriptor().getAnnotation();
		try {
			if (field != null) {
				annotation.entityClass().getDeclaredField(field.substring(1));
			}
			return true;
		} catch (final NoSuchFieldException e) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("property '" + field.substring(1) + "' doesn't exist").addConstraintViolation();
			return false;
		}
	}
}
