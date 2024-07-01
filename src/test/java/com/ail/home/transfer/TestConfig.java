package com.ail.home.transfer;

import java.time.Duration;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.web.client.RestOperations;

@TestConfiguration
public class TestConfig {

	/**
	 * Bean defined to successfully verify the execution of methods annotated with @Async
	 */
	@Bean
	public Executor taskExecutor() {
		return new SyncTaskExecutor();
	}

	/**
	 * Overriding pooled with a default RestTemplate.
	 */
	@Bean
	public RestOperations httpClient(final RestTemplateBuilder restTemplateBuilder,
		@Value("${http.client.connect.timeout}") final Duration connectTimeout,
		@Value("${http.client.read.timeout}") final Duration readTimeout) {
		return restTemplateBuilder
			.setConnectTimeout(connectTimeout)
			.setReadTimeout(readTimeout)
			.build();
	}
}
