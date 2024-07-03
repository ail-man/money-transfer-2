package com.ail.home.transfer.dto;

import java.util.UUID;

import com.ail.home.transfer.persistence.CustomerInfo;

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
public class CustomerData {

	private UUID id;
	private Integer version;
	@NotNull
	private Boolean enabled;
	@Valid
	private CustomerInfo info;
}
