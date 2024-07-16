package com.ail.home.transfer.repository;

import static com.ail.home.transfer.persistence.QAccount.account;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyBooleanFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyDateFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyIdFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyInStringCollectionFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyJsonbFieldValueEqualsFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.getFieldName;
import static com.ail.home.transfer.repository.impl.SearchUtils.getOrder;
import static com.ail.home.transfer.utils.SearchUtils.DEFAULT_LIMIT;

import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

import com.ail.home.transfer.dto.AccountCriteria;
import com.ail.home.transfer.persistence.Account;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Repository
@AllArgsConstructor
public class AccountRepoDsl {

	public static final String CREATED_AT = "createdAt";
	public static final String ACCOUNT_INFO_IBAN = "iban";

	private final JPAQueryFactory queryFactory;

	@Getter
	private final AccountRepo repo;

	public Long countAccounts(final AccountCriteria criteria) {
		return queryFactory
			.select(account.count())
			.from(account)
			.where(buildFilter(criteria))
			.fetchOne();
	}

	public Stream<Account> searchAccounts(final AccountCriteria criteria) {
		JPAQuery<Account> query = queryFactory
			.selectFrom(account)
			.where(buildFilter(criteria));

		query = applyOffset(query, criteria);
		query = applyLimit(query, criteria);
		query = applyOrder(query, criteria.getOrder());

		return query.stream();
	}

	private BooleanBuilder buildFilter(final AccountCriteria criteria) {
		BooleanBuilder predicate = new BooleanBuilder();
		predicate = applyIdFilter(predicate, account.id, criteria.getId());
		predicate = applyIdFilter(predicate, account.customerId, criteria.getCustomerId());
		predicate = applyBooleanFilter(predicate, account.enabled, criteria.getEnabled());
		predicate = applyDateFilter(predicate, account.createdAt, criteria.getCreatedFrom(), criteria.getCreatedTo());
		predicate = applyDateFilter(predicate, account.expiresAt, criteria.getExpiresFrom(), criteria.getExpiresTo());
		predicate = applyJsonbFieldValueEqualsFilter(predicate, account.info, ACCOUNT_INFO_IBAN, criteria.getIban());
		predicate = applyInStringCollectionFilter(predicate, account.currency, criteria.getCurrency());
		return predicate;
	}

	private JPAQuery<Account> applyOffset(final JPAQuery<Account> query, final AccountCriteria criteria) {
		final Long offset = criteria.getOffset();
		if (offset != null) {
			return query.offset(offset);
		}
		return query;
	}

	private JPAQuery<Account> applyLimit(JPAQuery<Account> query, final AccountCriteria criteria) {
		final Long limit = Objects.requireNonNullElse(criteria.getLimit(), DEFAULT_LIMIT);
		query = query.limit(limit);
		return query;
	}

	private JPAQuery<Account> applyOrder(JPAQuery<Account> query, final String criteriaOrder) {
		if (criteriaOrder != null) {
			query = query.orderBy(buildOrderBy(criteriaOrder));
			query = query.orderBy(buildOrderBy(criteriaOrder.charAt(0) + CREATED_AT));
		} else {
			query = query.orderBy(buildOrderBy("-" + CREATED_AT));
		}
		return query;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private OrderSpecifier<?> buildOrderBy(final String orderValue) {
		final Order order = getOrder(orderValue);
		final String fieldName = getFieldName(orderValue);
		final Path<Object> path = Expressions.path(Comparable.class, account, fieldName);
		return new OrderSpecifier(order, path);
	}
}
