package com.ail.home.transfer.exceptions;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -8066363327561216970L;

	public InternalServerException(final String message) {
		super(message);
	}

	public InternalServerException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
