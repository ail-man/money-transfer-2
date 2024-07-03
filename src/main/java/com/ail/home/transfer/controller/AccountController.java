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

import com.ail.home.transfer.dto.AccountCriteria;
import com.ail.home.transfer.dto.AccountDTO;
import com.ail.home.transfer.dto.AccountData;
import com.ail.home.transfer.service.AccountService;
import com.ail.home.transfer.service.JsonSerializationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/accounts")
@Slf4j
@AllArgsConstructor
public class AccountController {

	private final AccountService accountService;
	private final JsonSerializationService jsonSerializationService;

	@GetMapping
	public ResponseEntity<List<AccountDTO>> searchAccounts(@Valid final AccountCriteria accountCriteria) {
		final ServletUriComponentsBuilder urlBuilder = logRequestAndGetUriBuilder("Search accounts request: {}");
		final Long total = accountService.countAccounts(accountCriteria);
		final List<AccountDTO> result = accountService.searchAccounts(accountCriteria);
		final ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
			.header(XHeaders.TOTAL_COUNT, String.valueOf(total));
		buildNextOffsetLink(total, accountCriteria.getOffset(), accountCriteria.getLimit(), urlBuilder,
			responseBuilder);
		final ResponseEntity<List<AccountDTO>> response = responseBuilder.body(result);
		log.debug("Search accounts response: {}", response);
		return response;
	}

	@PostMapping
	public ResponseEntity<AccountDTO> createAccount(@RequestBody @Valid final AccountData accountData) {
		final String requestJson = jsonSerializationService.toJson(accountData);
		log.debug("Create account request: {}", requestJson);
		final AccountDTO result = accountService.createAccount(accountData);
		final URI location = createLocationHeader(result.getId());
		log.debug("Create account location: {}", location);
		final ResponseEntity<AccountDTO> response = ResponseEntity.created(location)
			.body(result);
		log.debug("Create account response: {}", response);
		return response;
	}

	@PutMapping
	public ResponseEntity<AccountDTO> updateAccount(@RequestBody @Valid final AccountData accountData) {
		final String requestJson = jsonSerializationService.toJson(accountData);
		log.debug("Update account request: {}", requestJson);
		final AccountDTO result = accountService.updateAccount(accountData);
		final ResponseEntity<AccountDTO> response = ResponseEntity.ok()
			.body(result);
		log.debug("Update account response: {}", response);
		return response;
	}
}
