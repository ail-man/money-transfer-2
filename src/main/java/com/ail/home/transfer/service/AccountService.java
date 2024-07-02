package com.ail.home.transfer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ail.home.transfer.dto.AccountCriteria;
import com.ail.home.transfer.dto.AccountDTO;
import com.ail.home.transfer.mapper.AccountMapper;
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
}
