package com.ail.home.transfer.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.ail.home.transfer.persistence.TransactionInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionData {

	private UUID id;
	@NotNull
	private BigDecimal amount;
	@NotNull
	private String currency;
	@NotNull
	private UUID fromAccountId;
	@NotNull
	private UUID toAccountId;
	@Valid
	private TransactionInfo info;

}
