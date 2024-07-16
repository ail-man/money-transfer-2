package com.ail.home.transfer.repository;

import static com.ail.home.transfer.persistence.QTransaction.transaction;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyAmountFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyDateFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyIdFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyInStringCollectionFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyJsonbFieldValueContainsFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.getFieldName;
import static com.ail.home.transfer.repository.impl.SearchUtils.getOrder;
import static com.ail.home.transfer.utils.SearchUtils.DEFAULT_LIMIT;

import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

import com.ail.home.transfer.dto.TransactionCriteria;
import com.ail.home.transfer.persistence.Transaction;
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
public class TransactionRepoDsl {

	public static final String TIMESTAMP = "timestamp";
	public static final String TRANSACTION_INFO_COMMENT = "comment";

	private final JPAQueryFactory queryFactory;

	@Getter
	private final TransactionRepo repo;

	public Long countTransactions(final TransactionCriteria criteria) {
		return queryFactory
			.select(transaction.count())
			.from(transaction)
			.where(buildFilter(criteria))
			.fetchOne();
	}

	public Stream<Transaction> searchTransactions(final TransactionCriteria criteria) {
		JPAQuery<Transaction> query = queryFactory
			.selectFrom(transaction)
			.where(buildFilter(criteria));

		query = applyOffset(query, criteria);
		query = applyLimit(query, criteria);
		query = applyOrder(query, criteria.getOrder());

		return query.stream();
	}

	private BooleanBuilder buildFilter(final TransactionCriteria criteria) {
		BooleanBuilder predicate = new BooleanBuilder();
		predicate = applyIdFilter(predicate, transaction.id, criteria.getId());
		predicate = applyIdFilter(predicate, transaction.fromAccountId, criteria.getFromAccountId());
		predicate = applyIdFilter(predicate, transaction.toAccountId, criteria.getToAccountId());
		predicate = applyInStringCollectionFilter(predicate, transaction.currency, criteria.getCurrency());
		predicate = applyAmountFilter(predicate, transaction.amount, criteria.getAmountFrom(), criteria.getAmountTo());
		predicate = applyDateFilter(predicate, transaction.timestamp, criteria.getTimestampFrom(), criteria.getTimestampTo());
		predicate = applyJsonbFieldValueContainsFilter(predicate, transaction.info, TRANSACTION_INFO_COMMENT, criteria.getComment());
		return predicate;
	}

	private JPAQuery<Transaction> applyOffset(final JPAQuery<Transaction> query, final TransactionCriteria criteria) {
		final Long offset = criteria.getOffset();
		if (offset != null) {
			return query.offset(offset);
		}
		return query;
	}

	private JPAQuery<Transaction> applyLimit(JPAQuery<Transaction> query, final TransactionCriteria criteria) {
		final Long limit = Objects.requireNonNullElse(criteria.getLimit(), DEFAULT_LIMIT);
		query = query.limit(limit);
		return query;
	}

	private JPAQuery<Transaction> applyOrder(JPAQuery<Transaction> query, final String criteriaOrder) {
		if (criteriaOrder != null) {
			query = query.orderBy(buildOrderBy(criteriaOrder));
			query = query.orderBy(buildOrderBy(criteriaOrder.charAt(0) + TIMESTAMP));
		} else {
			query = query.orderBy(buildOrderBy("-" + TIMESTAMP));
		}
		return query;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private OrderSpecifier<?> buildOrderBy(final String orderValue) {
		final Order order = getOrder(orderValue);
		final String fieldName = getFieldName(orderValue);
		final Path<Object> path = Expressions.path(Comparable.class, transaction, fieldName);
		return new OrderSpecifier(order, path);
	}
}
