package com.ail.home.transfer.dto;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ail.home.transfer.persistence.AccountInfo;

import jakarta.validation.Valid;
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
public class AccountData {

	private UUID id;
	private Integer version;
	private UUID customerId;
	private Boolean enabled;
	private BigInteger balance;
	private String currency;
	@Valid
	private AccountInfo info;
	private LocalDateTime expiresAt;
}
