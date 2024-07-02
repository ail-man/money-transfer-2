package com.ail.home.transfer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class BasicCriteria {

	public static final long DEFAULT_LIMIT = 20;

	// Searchable data

	private UUID id; // optional, strict equals

	private Boolean enabled; // optional, strict equals

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime createdFrom; // optional, inclusive

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime createdTo; // optional, inclusive

	// Pagination

	@Positive
	private Long offset; // optional

	@Min(1)
	@Max(1000)
	private Long limit; // optional

}
