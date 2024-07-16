package com.ail.home.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import com.ail.home.transfer.persistence.Customer;
import com.ail.home.transfer.validation.ValidateOrderPatternAndFieldName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TransactionCriteria extends PageCriteria {

	private UUID id; // optional, strict equals

	private UUID fromAccountId; // optional, strict equals
	private UUID toAccountId; // optional, strict equals

	private List<String> currency; // optional, 'in' condition
	private BigDecimal amountFrom; // optional, inclusive
	private BigDecimal amountTo; // optional, inclusive

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime timestampFrom; // optional, inclusive

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime timestampTo; // optional, inclusive

	private String comment; // optional, contains

	// Sorting
	@ValidateOrderPatternAndFieldName(entityClass = Customer.class)
	private String order; // optional, +fieldName or -fieldName
}
