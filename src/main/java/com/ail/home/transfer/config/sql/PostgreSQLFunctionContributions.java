package com.ail.home.transfer.config.sql;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;

/**
 * This class is intended to register any custom SQL function through the Hibernate 6
 * {@link FunctionContributions} interface.
 * The strategy used by Hibernate to load instances of {@link FunctionContributor} is the Service Provider Interface (SPI),
 * where class references are specified in the META-INF/services folder.
 */
public class PostgreSQLFunctionContributions implements FunctionContributor {

	@Override
	public void contributeFunctions(final FunctionContributions functionContributions) {
		registerJsonbPathEqualsQueryFunction(functionContributions);
		registerJsonbPathInQueryFunction(functionContributions);
	}

	private void registerJsonbPathEqualsQueryFunction(final FunctionContributions functionContributions) {
		final BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
		final SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();
		functionRegistry.registerPattern("jsonb_path_equals_func",
			"?1 #>> ?2 = ?3",
			basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
	}

	private void registerJsonbPathInQueryFunction(final FunctionContributions functionContributions) {
		final BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
		final SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();
		functionRegistry.registerPattern("jsonb_path_in_func",
			"?1 #>> ?2 = ANY (?3)",
			basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
	}
}
