package com.ail.home.transfer.service;

import static com.ail.home.transfer.utils.Utils.localDateTimeNow;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ail.home.transfer.dto.TransactionDTO;
import com.ail.home.transfer.dto.TransactionData;
import com.ail.home.transfer.exceptions.InvalidStateException;
import com.ail.home.transfer.mapper.TransactionMapper;
import com.ail.home.transfer.persistence.Account;
import com.ail.home.transfer.persistence.Transaction;
import com.ail.home.transfer.repository.AccountRepoDsl;
import com.ail.home.transfer.repository.TransactionRepoDsl;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransactionService {

	private final TransactionRepoDsl transactionRepoDsl;
	private final TransactionMapper transactionMapper;
	private final AccountRepoDsl accountRepoDsl;

	@Transactional
	public TransactionDTO createTransaction(final TransactionData transactionData) {
		final Transaction transaction = transactionMapper.map(transactionData);
		if (transaction.getId() == null) {
			transaction.setId(UUID.randomUUID());
		}
		final LocalDateTime timestamp = localDateTimeNow();
		transaction.setTimestamp(timestamp);

		final Account fromAccount = accountRepoDsl.getRepo().findLockedByIdOrFail(transaction.getFromAccountId());
		final Account toAccount = accountRepoDsl.getRepo().findLockedByIdOrFail(transaction.getToAccountId());

		final String transactionCurrency = transaction.getCurrency();
		final String fromAccountCurrency = fromAccount.getCurrency();
		final String toAccountCurrency = toAccount.getCurrency();

		if (!isAllCurrenciesAreTheSame(transactionCurrency, fromAccountCurrency, toAccountCurrency)) {
			throw new InvalidStateException("Currency conversion is not yet implemented");
		}

		final BigDecimal transactionAmount = transaction.getAmount();
		final BigDecimal fromBalance = fromAccount.getBalance();
		final BigDecimal toBalance = toAccount.getBalance();

		// Allowing negative balance

		final BigDecimal resultFromBalance = fromBalance.subtract(transactionAmount);
		fromAccount.setBalance(resultFromBalance);
		fromAccount.setUpdatedAt(timestamp);

		final BigDecimal resultToBalance = toBalance.add(transactionAmount);
		toAccount.setBalance(resultToBalance);
		toAccount.setUpdatedAt(timestamp);

		final Transaction createdTransaction = transactionRepoDsl.getRepo().saveAndFlush(transaction);
		accountRepoDsl.getRepo().saveAndFlush(fromAccount);
		accountRepoDsl.getRepo().saveAndFlush(toAccount);

		return transactionMapper.map(createdTransaction);
	}

	private static boolean isAllCurrenciesAreTheSame(final String transactionCurrency, final String fromAccountCurrency,
		final String toAccountCurrency) {
		return transactionCurrency.equals(fromAccountCurrency) && transactionCurrency.equals(toAccountCurrency);
	}
}
