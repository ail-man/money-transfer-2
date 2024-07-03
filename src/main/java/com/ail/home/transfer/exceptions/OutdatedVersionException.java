package com.ail.home.transfer.exceptions;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class OutdatedVersionException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -4386964118764891399L;

	public OutdatedVersionException(final String message) {
		super(message);
	}
}
