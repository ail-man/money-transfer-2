package com.ail.home.transfer.utils;

import com.ail.home.transfer.exceptions.OutdatedVersionException;
import com.ail.home.transfer.exceptions.RequestValidationException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtils {

	public static void validateEntityVersion(final Integer expectedVersion, final Integer actualVersion) {
		if (expectedVersion == null) {
			throw new RequestValidationException("Version cannot be null");
		}
		if (!expectedVersion.equals(actualVersion)) {
			throw new OutdatedVersionException(
				"Version " + expectedVersion + " does not match the current entity version " + actualVersion);
		}
	}
}
