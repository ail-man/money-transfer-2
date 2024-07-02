package com.ail.home.transfer.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ControllerTestUtils {

	public static UriComponentsBuilder defaultUriBuilder(final String path) {
		return UriComponentsBuilder.fromUriString(path);
	}

	public static URI getLinkFromHeader(final MockHttpServletResponse servletResponse, final String path) throws URISyntaxException {
		// validate URI: <http://localhost/...?limit=2&offset=2>; rel=\"next\"
		final String linkHeaderValue = servletResponse.getHeader(HttpHeaders.LINK);
		assertThat(linkHeaderValue).isNotBlank();
		final String link = linkHeaderValue.replaceAll("<|>;\\srel=\"next\"", "");
		final URI nextUri = new URI(link);
		assertThat(nextUri).hasPath(path);
		return nextUri;
	}
}
