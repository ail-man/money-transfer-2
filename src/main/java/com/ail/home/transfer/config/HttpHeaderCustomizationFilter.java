package com.ail.home.transfer.config;

import static com.ail.home.transfer.controller.XHeaders.RESPONSE_TIMESTAMP;
import static com.ail.home.transfer.utils.Utils.localDateTimeNowString;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class HttpHeaderCustomizationFilter implements Filter {

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
		final FilterChain chain) throws IOException, ServletException {
		final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader(RESPONSE_TIMESTAMP, localDateTimeNowString());
		chain.doFilter(request, response);
	}

}
