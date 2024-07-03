package com.ail.home.transfer.exceptions;

import java.io.Serial;
import java.text.MessageFormat;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -7652322697128246321L;

	private static final String CUSTOMER_ID_NOT_FOUND_MESSAGE =
		"Customer with 'id' [{0}] not found";

	private static final String ACCOUNT_ID_NOT_FOUND_MESSAGE =
		"Customer with 'id' [{0}] not found";

	private static final String TRANSACTION_ID_NOT_FOUND_MESSAGE =
		"Transaction with 'id' [{0}] not found";

	public EntityNotFoundException(final String message) {
		super(message);
	}

	public static EntityNotFoundException customerIdNotFound(final UUID customerId) {
		return new EntityNotFoundException(MessageFormat.format(CUSTOMER_ID_NOT_FOUND_MESSAGE, customerId));
	}

	public static EntityNotFoundException accountIdNotFound(final UUID customerId) {
		return new EntityNotFoundException(MessageFormat.format(ACCOUNT_ID_NOT_FOUND_MESSAGE, customerId));
	}

	public static EntityNotFoundException transactionIdNotFound(final UUID transactionId) {
		return new EntityNotFoundException(MessageFormat.format(TRANSACTION_ID_NOT_FOUND_MESSAGE, transactionId));
	}
}
