package com.ail.home.transfer.config.async;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

public class MdcTaskDecorator implements TaskDecorator {

	@Override
	public Runnable decorate(final Runnable runnable) {
		final Map<String, String> contextMap = MDC.getCopyOfContextMap();
		return () -> {
			try {
				if (contextMap != null) {
					MDC.setContextMap(contextMap);
				}
				runnable.run();
			} finally {
				MDC.clear();
			}
		};
	}
}
