package com.cloudwaer.common.core.util;

import com.cloudwaer.common.core.exception.BusinessException;

/**
 * Simple argument preconditions.
 *
 * <p>
 * Use this for service/controller guard checks to keep business code readable.
 */
public final class Preconditions {

	private Preconditions() {
	}

	public static <T> T requireNonNull(T value, String message) {
		if (value == null) {
			throw new BusinessException(message);
		}
		return value;
	}

	public static String requireNonBlank(String value, String message) {
		if (value == null || value.isBlank()) {
			throw new BusinessException(message);
		}
		return value;
	}

}
