package com.ail.home.transfer.repository.impl.sqldialect;

import org.hibernate.dialect.PostgreSQLDialect;

import jakarta.persistence.LockModeType;

/**
 * This class is intended to override the default behavior of Hibernate dialect for PostgreSQL in order to not add {@code nowait} option
 * to queries when {@link LockModeType#PESSIMISTIC_FORCE_INCREMENT} is used.
 * See this <a href="https://forum.hibernate.org/viewtopic.php?p=2474975">discussion topic</a> for details about the problem which it will
 * solve.
 *
 * @author Artur Lomsadze
 */
public class PostgreSQLNoNowaitDialect extends PostgreSQLDialect {

	@Override
	public String getForUpdateNowaitString() {
		return super.getForUpdateString();
	}

	@Override
	public String getForUpdateNowaitString(String aliases) {
		return super.getForUpdateString(aliases);
	}
}
