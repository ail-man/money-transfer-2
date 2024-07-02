package com.ail.home.transfer.controller;

import static com.ail.home.transfer.controller.ControllerUtils.buildNextOffsetLink;
import static com.ail.home.transfer.controller.ControllerUtils.logRequestAndGetUriBuilder;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ail.home.transfer.dto.AccountCriteria;
import com.ail.home.transfer.dto.AccountDTO;
import com.ail.home.transfer.service.AccountService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/accounts")
@Slf4j
@AllArgsConstructor
public class AccountController {

	private final AccountService accountService;

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
}
