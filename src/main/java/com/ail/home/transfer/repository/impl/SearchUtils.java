package com.ail.home.transfer.repository.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchUtils {

	public static BooleanBuilder applyIdFilter(final BooleanBuilder predicate, final ComparablePath<UUID> path, final UUID value) {
		if (value != null && StringUtils.isNotBlank(value.toString())) {
			return predicate.and(path.eq(value));
		}
		return predicate;
	}

	public static BooleanBuilder applyBooleanFilter(final BooleanBuilder predicate, final BooleanPath path,
		final Boolean value) {
		if (value != null) {
			return predicate.and(path.eq(value));
		}
		return predicate;
	}

	public static BooleanBuilder applyStringFilter(final BooleanBuilder predicate, final StringPath path, final String value) {
		if (StringUtils.isNotBlank(value)) {
			return predicate.and(path.eq(value));
		}
		return predicate;
	}

	public static BooleanBuilder applyInStringCollectionFilter(final BooleanBuilder predicate, final StringPath path,
		final List<String> values) {
		if (ObjectUtils.isNotEmpty(values)) {
			return predicate.and(path.in(values));
		}
		return predicate;
	}

	public static BooleanBuilder applyDateFilter(final BooleanBuilder predicate, final DateTimePath<LocalDateTime> path,
		final LocalDateTime dateFrom, final LocalDateTime dateTo) {
		if (dateFrom != null && dateTo != null) {
			return predicate.and(path.between(dateFrom, dateTo));
		}
		if (dateFrom == null && dateTo != null) {
			return predicate.and(path.loe(dateTo));
		}
		if (dateFrom != null) {
			return predicate.and(path.goe(dateFrom));
		}
		return predicate;
	}

	public static String getFieldName(final String orderValue) {
		return orderValue.substring(1);
	}

	public static Order getOrder(final String orderValue) {
		final char orderChar = orderValue.charAt(0);
		final OrderType orderType = OrderType.of(orderChar);
		return orderType.getOrder();
	}

	public static BooleanBuilder applyJsonbFilterEquals(final BooleanBuilder predicate, final Path<?> jsonbPath, final String searchKey,
		final String value) {
		if (StringUtils.isNotBlank(value)) {
			return predicate.and(buildJsonbEqualityExpression(jsonbPath, searchKey, value));
		}
		return predicate;
	}

	public static BooleanBuilder applyJsonbFilterIn(final BooleanBuilder predicate, final Path<?> jsonbPath, final String searchKey,
		final List<String> value) {
		if (CollectionUtils.isNotEmpty(value)) {
			return predicate.and(buildJsonbInExpression(jsonbPath, searchKey, value));
		}
		return predicate;
	}

	// TODO check SQL injections
	public static BooleanTemplate buildJsonbEqualityExpression(final Path<?> jsonbPath, final String searchKey, final String value) {
		final String[] jsonKeys = searchKey.split("\\.");
		return Expressions.booleanTemplate("jsonb_path_equals_func({0}, {1}, {2})", jsonbPath, jsonKeys, value);
	}

	// TODO check SQL injections
	public static BooleanTemplate buildJsonbInExpression(final Path<?> jsonbPath, final String searchKey, final List<String> value) {
		final String[] jsonKeys = searchKey.split("\\.");
		// Convert the list to an array format that QueryDSL can handle
		final String[] valueArray = value.toArray(new String[0]);
		return Expressions.booleanTemplate("jsonb_path_in_func({0}, {1}, {2})", jsonbPath, jsonKeys, valueArray);
	}

	public static BooleanBuilder applyAmountFilter(final BooleanBuilder predicate,
		final BigDecimal amountFrom, final BigDecimal amountTo, final NumberPath<BigDecimal> amountPath) {
		if (amountFrom != null && amountTo != null) {
			return predicate.and(amountPath.between(amountFrom, amountTo));
		}
		if (amountFrom == null && amountTo != null) {
			return predicate.and(amountPath.loe(amountTo));
		}
		if (amountFrom != null) {
			return predicate.and(amountPath.goe(amountFrom));
		}
		return predicate;
	}
}
