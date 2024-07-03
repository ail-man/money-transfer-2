package com.ail.home.transfer.controller;

import static com.ail.home.transfer.controller.ControllerUtils.buildNextOffsetLink;
import static com.ail.home.transfer.controller.ControllerUtils.createLocationHeader;
import static com.ail.home.transfer.controller.ControllerUtils.logRequestAndGetUriBuilder;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ail.home.transfer.dto.CustomerCriteria;
import com.ail.home.transfer.dto.CustomerDTO;
import com.ail.home.transfer.dto.CustomerData;
import com.ail.home.transfer.service.CustomerService;
import com.ail.home.transfer.service.JsonSerializationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/customers")
@Slf4j
@AllArgsConstructor
public class CustomerController {

	private final CustomerService customerService;
	private final JsonSerializationService jsonSerializationService;

	@GetMapping
	public ResponseEntity<List<CustomerDTO>> searchCustomers(@Valid final CustomerCriteria customerCriteria) {
		final ServletUriComponentsBuilder urlBuilder = logRequestAndGetUriBuilder("Search customers request: {}");
		final Long total = customerService.countCustomers(customerCriteria);
		final List<CustomerDTO> result = customerService.searchCustomers(customerCriteria);
		final ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
			.header(XHeaders.TOTAL_COUNT, String.valueOf(total));
		buildNextOffsetLink(total, customerCriteria.getOffset(), customerCriteria.getLimit(), urlBuilder,
			responseBuilder);
		final ResponseEntity<List<CustomerDTO>> response = responseBuilder.body(result);
		log.debug("Search customers response: {}", response);
		return response;
	}

	@PostMapping
	public ResponseEntity<CustomerDTO> createCustomer(@RequestBody @Valid final CustomerData customerData) {
		final String requestJson = jsonSerializationService.toJson(customerData);
		log.debug("Create customer request: {}", requestJson);
		final CustomerDTO result = customerService.createCustomer(customerData);
		final URI location = createLocationHeader(result.getId());
		log.debug("Create customer location: {}", location);
		final ResponseEntity<CustomerDTO> response = ResponseEntity.created(location)
			.body(result);
		log.debug("Create customer response: {}", response);
		return response;
	}

	@PutMapping
	public ResponseEntity<CustomerDTO> updateCustomer(@RequestBody @Valid final CustomerData customerData) {
		final String requestJson = jsonSerializationService.toJson(customerData);
		log.debug("Update customer request: {}", requestJson);
		final CustomerDTO result = customerService.updateCustomer(customerData);
		final ResponseEntity<CustomerDTO> response = ResponseEntity.ok()
			.body(result);
		log.debug("Update customer response: {}", response);
		return response;
	}
}
