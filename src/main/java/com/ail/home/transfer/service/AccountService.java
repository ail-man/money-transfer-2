package com.ail.home.transfer.service;

import static com.ail.home.transfer.utils.Utils.localDateTimeNow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ail.home.transfer.dto.AccountCriteria;
import com.ail.home.transfer.dto.AccountDTO;
import com.ail.home.transfer.dto.AccountData;
import com.ail.home.transfer.mapper.AccountMapper;
import com.ail.home.transfer.persistence.Account;
import com.ail.home.transfer.repository.AccountRepoDsl;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {

	private final AccountRepoDsl accountRepoDsl;
	private final AccountMapper accountMapper;

	public Long countAccounts(final AccountCriteria accountCriteria) {
		return accountRepoDsl.countAccounts(accountCriteria);
	}

	public List<AccountDTO> searchAccounts(final AccountCriteria accountCriteria) {
		return accountRepoDsl.searchAccounts(accountCriteria)
			.map(accountMapper::map)
			.toList();
	}

	public AccountDTO createAccount(final AccountData accountData) {
		final Account account = accountMapper.map(accountData);
		account.setId(UUID.randomUUID());
		final LocalDateTime timestamp = localDateTimeNow();
		account.setCreatedAt(timestamp);
		account.setUpdatedAt(timestamp);
		final Account createdAccount = accountRepoDsl.getRepo().saveAndFlush(account);
		return accountMapper.map(createdAccount);
	}
}
