package com.ail.home.transfer.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class XHeaders {

	public static final String RESPONSE_TIMESTAMP = "X-Response-Timestamp";
	public static final String TOTAL_COUNT = "X-Total-Count";
	public static final String ENTITY_VERSION = "X-Entity-Version";
}
