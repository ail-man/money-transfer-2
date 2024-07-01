package com.ail.home.transfer.exceptions;

import java.io.Serial;

public class OutdatedVersionException extends InvalidStateException {

	@Serial
	private static final long serialVersionUID = -4386964118764891399L;

	public OutdatedVersionException(final String message) {
		super(message);
	}
}
