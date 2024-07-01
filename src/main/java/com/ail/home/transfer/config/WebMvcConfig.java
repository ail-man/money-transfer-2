package com.ail.home.transfer.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.ail.home.transfer.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebMvcConfig {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
		final JsonSerializer<LocalDateTime> serializerLocalDateTime = new StdSerializer<>(LocalDateTime.class) {
			@Override
			public void serialize(final LocalDateTime localDateTime, final JsonGenerator jsonGenerator,
				final SerializerProvider serializerProvider)
				throws IOException {
				jsonGenerator.writeString(Utils.localDateTimeString(localDateTime));
			}
		};
		final JsonSerializer<Date> serializerDate = new StdSerializer<>(Date.class) {
			public void serialize(final Date date, final JsonGenerator jsonGenerator,
				final SerializerProvider serializerProvider)
				throws IOException {
				jsonGenerator.writeString(Utils.localDateTimeString(date));
			}
		};
		return builder -> builder.serializers(serializerLocalDateTime, serializerDate);
	}

	@Bean
	public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
		final FilterRegistrationBean<ForwardedHeaderFilter> bean = new FilterRegistrationBean<>();
		final ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
		bean.setFilter(filter);
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer(@Value("${cors.domains}") final String[] corsDomains) {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NonNull final CorsRegistry registry) {
				log.info("CORS.domains: {}", (Object) corsDomains);
				registry.addMapping("/**")
					.allowedOrigins(corsDomains)
					.allowedMethods(
						HttpMethod.GET.toString(),
						HttpMethod.POST.toString(),
						HttpMethod.PUT.toString(),
						HttpMethod.DELETE.toString(),
						HttpMethod.OPTIONS.toString())
					.allowedHeaders("*")
					.exposedHeaders("*");
			}
		};
	}
}

