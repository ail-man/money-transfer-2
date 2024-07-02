package com.ail.home.transfer.controller;

import static com.ail.home.transfer.controller.ControllerUtils.createLocationHeader;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
