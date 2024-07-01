package com.ail.home.transfer.repository.impl;

import java.util.Arrays;

import com.querydsl.core.types.Order;

import lombok.Getter;

@Getter
public enum OrderType {
	ASC('+', Order.ASC),
	DESC('-', Order.DESC);

	private final char orderChar;
	private final Order order;

	OrderType(final char orderChar, final Order order) {
		this.orderChar = orderChar;
		this.order = order;
	}

	public static OrderType of(final char orderChar) {
		return Arrays.stream(values())
			.filter(t -> t.orderChar == orderChar)
			.findFirst()
			.orElseThrow();
	}
}
