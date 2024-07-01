package com.ail.home.transfer.config.async;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.AllArgsConstructor;

@Configuration
@EnableAsync
@EnableConfigurationProperties(TaskExecutionProperties.class)
@AllArgsConstructor
public class ThreadPoolExecutorConfig {

	private final TaskExecutionProperties properties;

	@Bean
	public TaskExecutor taskExecutor() {
		final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		final TaskExecutionProperties.Pool poolProperties = properties.getPool();
		threadPoolTaskExecutor.setMaxPoolSize(poolProperties.getMaxSize());
		threadPoolTaskExecutor.setCorePoolSize(poolProperties.getCoreSize());
		threadPoolTaskExecutor.setQueueCapacity(poolProperties.getQueueCapacity());
		threadPoolTaskExecutor.setKeepAliveSeconds((int) poolProperties.getKeepAlive().toSeconds());
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		threadPoolTaskExecutor.setThreadNamePrefix(properties.getThreadNamePrefix());
		threadPoolTaskExecutor.setTaskDecorator(new MdcTaskDecorator());
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}

}
