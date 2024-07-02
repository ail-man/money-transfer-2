package com.ail.home.transfer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ail.home.transfer.persistence.AccountInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountDTO {

	private UUID id;
	private Integer version;
	private Boolean enabled;
	private AccountInfo info;
	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;
}
