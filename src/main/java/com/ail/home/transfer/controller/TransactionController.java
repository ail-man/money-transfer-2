package com.ail.home.transfer.controller;

import static com.ail.home.transfer.controller.ControllerUtils.buildNextOffsetLink;
import static com.ail.home.transfer.controller.ControllerUtils.createLocationHeader;
import static com.ail.home.transfer.controller.ControllerUtils.logRequestAndGetUriBuilder;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ail.home.transfer.dto.TransactionCriteria;
import com.ail.home.transfer.dto.TransactionDTO;
import com.ail.home.transfer.dto.TransactionData;
import com.ail.home.transfer.service.JsonSerializationService;
import com.ail.home.transfer.service.TransactionService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/transactions")
@Slf4j
@AllArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;
	private final JsonSerializationService jsonSerializationService;

	@GetMapping
	public ResponseEntity<List<TransactionDTO>> searchTransactions(@Valid final TransactionCriteria transactionCriteria) {
		final ServletUriComponentsBuilder urlBuilder = logRequestAndGetUriBuilder("Search transactions request: {}");
		final Long total = transactionService.countTransactions(transactionCriteria);
		final List<TransactionDTO> result = transactionService.searchTransactions(transactionCriteria);
		final ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
			.header(XHeaders.TOTAL_COUNT, String.valueOf(total));
		buildNextOffsetLink(total, transactionCriteria.getOffset(), transactionCriteria.getLimit(), urlBuilder,
			responseBuilder);
		final ResponseEntity<List<TransactionDTO>> response = responseBuilder.body(result);
		log.debug("Search transactions response: {}", response);
		return response;
	}

	@PostMapping
	public ResponseEntity<TransactionDTO> createTransaction(@RequestBody @Valid final TransactionData transactionData) {
		final String requestJson = jsonSerializationService.toJson(transactionData);
		log.debug("Create a new transaction request: {}", requestJson);
		final TransactionDTO result = transactionService.createTransaction(transactionData);
		final URI location = createLocationHeader(result.getId());
		log.debug("Create a new transaction location: {}", location);
		final ResponseEntity<TransactionDTO> response = ResponseEntity.created(location)
			.body(result);
		log.debug("Create a new transaction response: {}", response);
		return response;
	}
}
