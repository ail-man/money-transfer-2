package com.ail.home.transfer.config.sql;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Configuration
public class QueryDSLConfig {

	@Bean
	public JPAQueryFactory queryFactory(final EntityManager entityManager) {
		return new JPAQueryFactory(entityManager);
	}

}
