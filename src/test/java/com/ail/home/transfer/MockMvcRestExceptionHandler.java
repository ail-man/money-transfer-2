package com.ail.home.transfer;

import java.util.Map;
import java.util.Optional;

import org.junit.platform.commons.util.AnnotationUtils;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import lombok.AllArgsConstructor;

/**
 * Necessary ControllerAdvice for test purposes. MockMvc is not a real servlet environment,
 * therefore it does not redirect error responses to [ErrorController], which produces a validation response.
 * Without it MockMvc can only map HttpStatus codes with empty body.
 */
@ControllerAdvice
@AllArgsConstructor
public class MockMvcRestExceptionHandler extends ResponseEntityExceptionHandler {

	private final BasicErrorController errorController;

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleGenericException(final Exception ex, final WebRequest request) {
		HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR;
		final Optional<ResponseStatus> responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
		if (responseStatus.isPresent()) {
			status = responseStatus.get().value();
		}
		return handleExceptionInternal(ex, null, null, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(final Exception ex, final Object body, final HttpHeaders headers,
		final HttpStatusCode status, final WebRequest request) {
		request.setAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE, status.value(), WebRequest.SCOPE_REQUEST);
		request.setAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE, ((ServletWebRequest) request).getRequest().getRequestURI(),
			WebRequest.SCOPE_REQUEST);

		final ResponseEntity<Map<String, Object>> errorControllerResponseEntity = errorController
			.error(((ServletWebRequest) request).getRequest());

		return new ResponseEntity<>(errorControllerResponseEntity.getBody(), errorControllerResponseEntity.getHeaders(),
			errorControllerResponseEntity.getStatusCode());
	}
}
