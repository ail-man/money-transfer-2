package com.ail.home.transfer.exceptions;

import java.io.Serial;

public class JsonSerializationException extends InternalServerException {

	@Serial
	private static final long serialVersionUID = -7246548714737750134L;

	public JsonSerializationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
