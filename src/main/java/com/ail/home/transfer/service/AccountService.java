package com.ail.home.transfer.service;

import static com.ail.home.transfer.utils.Utils.localDateTimeNow;
import static com.ail.home.transfer.utils.ValidationUtils.validateEntityVersion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public AccountDTO createAccount(final AccountData accountData) {
		final Account account = accountMapper.map(accountData);
		account.setVersion(0);
		account.setBalance(new BigDecimal(0));
		if (account.getId() == null) {
			account.setId(UUID.randomUUID());
		}
		final LocalDateTime timestamp = localDateTimeNow();
		account.setCreatedAt(timestamp);
		account.setUpdatedAt(timestamp);
		final Account createdAccount = accountRepoDsl.getRepo().saveAndFlush(account);
		return accountMapper.map(createdAccount);
	}

	@Transactional
	public AccountDTO updateAccount(final AccountData accountData) {
		Account account = accountRepoDsl.getRepo().findLockedByIdOrFail(accountData.getId());
		validateEntityVersion(accountData.getVersion(), account.getVersion());
		accountMapper.map(accountData, account);
		account.setUpdatedAt(localDateTimeNow());
		final Account updatedAccount = accountRepoDsl.getRepo().saveAndFlush(account);
		return accountMapper.map(updatedAccount);
	}
}
