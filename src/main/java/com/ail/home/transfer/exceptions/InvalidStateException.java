package com.ail.home.transfer.exceptions;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class InvalidStateException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 5274874792282225823L;

	public InvalidStateException(final String message) {
		super(message);
	}
}
