package com.ail.home.transfer.exceptions;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown whenever the request body or request parameters contain invalid or missing values
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RequestValidationException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -2391044062514142719L;

	public RequestValidationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public RequestValidationException(final String message) {
		super(message);
	}
}
