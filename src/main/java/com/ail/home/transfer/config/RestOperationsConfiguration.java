package com.ail.home.transfer.config;

import java.time.Duration;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;

@Configuration
public class RestOperationsConfiguration {

	@Bean
	public RestOperations httpClient(@Value("${http.pool.time.to.live}") final Duration timeToLive,
		@Value("${http.pool.validation.period}") final Duration validationPeriod,
		@Value("${http.client.connect.timeout}") final Duration connectTimeout,
		@Value("${http.client.read.timeout}") final Duration readTimeout,
		@Value("${http.pool.max.connections.total}") final int maxConnections,
		@Value("${http.pool.max.connections.per.route}") final int maxPerRoute,
		final RestTemplateBuilder restTemplateBuilder) {
		final ConnectionConfig connectionConfig = ConnectionConfig.custom()
			.setTimeToLive(TimeValue.of(timeToLive))
			.setValidateAfterInactivity(TimeValue.of(validationPeriod))
			.setConnectTimeout(Timeout.of(connectTimeout))
			.setSocketTimeout(Timeout.of(readTimeout))
			.build();

		final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setDefaultConnectionConfig(connectionConfig);
		connectionManager.setMaxTotal(maxConnections);
		connectionManager.setDefaultMaxPerRoute(maxPerRoute);

		// Build the HttpClient with the connection manager.
		final CloseableHttpClient httpClient = HttpClients.custom()
			.setConnectionManager(connectionManager)
			.build();

		// Integrating Spring's RestTemplate with the HttpClient instance that we have configured with our pooled connection manager.
		return restTemplateBuilder
			.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient))
			.build();
	}
}
