package com.ail.home.transfer.controller;

import static com.ail.home.transfer.utils.SearchUtils.DEFAULT_LIMIT;
import static com.ail.home.transfer.utils.SearchUtils.DEFAULT_OFFSET;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ControllerUtils {

	public static ServletUriComponentsBuilder logRequestAndGetUriBuilder(final String message) {
		final ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
		final String originalUrl = builder.toUriString();
		log.debug(message, originalUrl);
		return builder;
	}

	public static void buildNextOffsetLink(final Long total, final Long offset, final Long limit,
		final ServletUriComponentsBuilder uriBuilder, final ResponseEntity.BodyBuilder responseBuilder) {

		final long actualOffset = offset != null ? offset : DEFAULT_OFFSET;
		final long actualLimit = limit != null ? limit : DEFAULT_LIMIT;

		if (total > actualOffset + actualLimit) {
			final long nextOffset = actualOffset + actualLimit;
			final String nextUrl = uriBuilder.replaceQueryParam("offset", String.valueOf(nextOffset))
				.build().toUriString();
			responseBuilder.header(HttpHeaders.LINK, "<" + nextUrl + ">; rel=\"next\"");
		}
	}

	public static URI createLocationHeader(final UUID entityId) {
		return ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(entityId.toString())
			.toUri();
	}
}
