package com.ail.home.transfer.dto;

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
public class CustomerCriteria extends BasicCriteria {

	private String email; // optional

	// Sorting
	@ValidateOrderPatternAndFieldName(entityClass = Customer.class)
	private String order; // optional, +fieldName or -fieldName
}
