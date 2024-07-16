package com.ail.home.transfer.repository;

import static com.ail.home.transfer.persistence.QCustomer.customer;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyBooleanFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyDateFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyIdFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.applyJsonbFieldValueInCollectionFilter;
import static com.ail.home.transfer.repository.impl.SearchUtils.getFieldName;
import static com.ail.home.transfer.repository.impl.SearchUtils.getOrder;
import static com.ail.home.transfer.utils.SearchUtils.DEFAULT_LIMIT;

import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

import com.ail.home.transfer.dto.CustomerCriteria;
import com.ail.home.transfer.persistence.Customer;
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
public class CustomerRepoDsl {

	public static final String CREATED_AT = "createdAt";
	public static final String CUSTOMER_INFO_EMAIL = "email";

	private final JPAQueryFactory queryFactory;

	@Getter
	private final CustomerRepo repo;

	public Long countCustomers(final CustomerCriteria criteria) {
		return queryFactory
			.select(customer.count())
			.from(customer)
			.where(buildFilter(criteria))
			.fetchOne();
	}

	public Stream<Customer> searchCustomers(final CustomerCriteria criteria) {
		JPAQuery<Customer> query = queryFactory
			.selectFrom(customer)
			.where(buildFilter(criteria));

		query = applyOffset(query, criteria);
		query = applyLimit(query, criteria);
		query = applyOrder(query, criteria.getOrder());

		return query.stream();
	}

	private BooleanBuilder buildFilter(final CustomerCriteria criteria) {
		BooleanBuilder predicate = new BooleanBuilder();
		predicate = applyIdFilter(predicate, customer.id, criteria.getId());
		predicate = applyBooleanFilter(predicate, customer.enabled, criteria.getEnabled());
		predicate = applyDateFilter(predicate, customer.createdAt, criteria.getCreatedFrom(), criteria.getCreatedTo());
		predicate = applyDateFilter(predicate, customer.updatedAt, criteria.getUpdatedFrom(), criteria.getUpdatedTo());
		predicate = applyJsonbFieldValueInCollectionFilter(predicate, customer.info, CUSTOMER_INFO_EMAIL, criteria.getEmail());
		return predicate;
	}

	private JPAQuery<Customer> applyOffset(final JPAQuery<Customer> query, final CustomerCriteria criteria) {
		final Long offset = criteria.getOffset();
		if (offset != null) {
			return query.offset(offset);
		}
		return query;
	}

	private JPAQuery<Customer> applyLimit(JPAQuery<Customer> query, final CustomerCriteria criteria) {
		final Long limit = Objects.requireNonNullElse(criteria.getLimit(), DEFAULT_LIMIT);
		query = query.limit(limit);
		return query;
	}

	private JPAQuery<Customer> applyOrder(JPAQuery<Customer> query, final String criteriaOrder) {
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
		final Path<Object> path = Expressions.path(Comparable.class, customer, fieldName);
		return new OrderSpecifier(order, path);
	}
}
