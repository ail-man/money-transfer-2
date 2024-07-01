package com.ail.home.transfer.exceptions;

import java.io.Serial;
import java.text.MessageFormat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -7652322697128246321L;

	private static final String CUSTOMER_ID_NOT_FOUND_MESSAGE =
		"Customer with 'id' [{0}] not found";

	public EntityNotFoundException(final String message) {
		super(message);
	}

	public static EntityNotFoundException customerIdNotFound(final String customerId) {
		return new EntityNotFoundException(MessageFormat.format(CUSTOMER_ID_NOT_FOUND_MESSAGE, customerId));
	}
}
