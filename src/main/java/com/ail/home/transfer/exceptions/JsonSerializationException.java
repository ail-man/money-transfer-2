package com.ail.home.transfer.exceptions;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class JsonSerializationException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -7246548714737750134L;

	public JsonSerializationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
