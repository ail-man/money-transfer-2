package com.ail.home.transfer.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Repository
@AllArgsConstructor
public class TransactionRepoDsl {

	private final JPAQueryFactory queryFactory;

	@Getter
	private final TransactionRepo repo;

}
