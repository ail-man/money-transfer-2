package com.ail.home.transfer.utils;

import com.ail.home.transfer.exceptions.OutdatedVersionException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtils {

	public static void validateEntityVersion(final Integer actualVersion, final Integer expectedVersion) {
		if (expectedVersion == null) {
			return;
		}
		if (!expectedVersion.equals(actualVersion)) {
			throw new OutdatedVersionException("Version " + actualVersion + " does not match expected version " + expectedVersion);
		}
	}
}
