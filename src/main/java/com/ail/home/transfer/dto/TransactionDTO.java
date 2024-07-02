package com.ail.home.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ail.home.transfer.persistence.TransactionInfo;

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
public class TransactionDTO {

	private UUID id;
	private LocalDateTime timestamp;
	private BigDecimal amount;
	private String currency;
	private UUID fromAccountId;
	private UUID toAccountId;
	private TransactionInfo info;

}
