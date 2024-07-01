package com.ail.home.transfer.service;

import static com.ail.home.transfer.utils.Utils.localDateTimeNow;
import static com.ail.home.transfer.utils.ValidationUtils.validateEntityVersion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ail.home.transfer.dto.CustomerCriteria;
import com.ail.home.transfer.dto.CustomerDTO;
import com.ail.home.transfer.dto.CustomerData;
import com.ail.home.transfer.mapper.CustomerMapper;
import com.ail.home.transfer.persistence.Customer;
import com.ail.home.transfer.repository.CustomerRepoDsl;
import com.ail.home.transfer.utils.BeanUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomerService {

	private final CustomerRepoDsl customerRepoDsl;
	private final CustomerMapper customerMapper;

	public Long countCustomers(final CustomerCriteria customerCriteria) {
		return customerRepoDsl.countCustomers(customerCriteria);
	}

	public List<CustomerDTO> searchCustomers(final CustomerCriteria customerCriteria) {
		return customerRepoDsl.searchCustomers(customerCriteria)
			.map(customerMapper::map)
			.toList();
	}

	@Transactional
	public CustomerDTO createCustomer(final CustomerData customerData) {
		final Customer customer = customerMapper.map(customerData);
		customer.setId(UUID.randomUUID());
		final LocalDateTime timestamp = localDateTimeNow();
		customer.setCreatedAt(timestamp);
		customer.setUpdatedAt(timestamp);
		final Customer createdCustomer = customerRepoDsl.getRepo().save(customer);
		return customerMapper.map(createdCustomer);
	}

	@Transactional
	public CustomerDTO updateCustomer(final UUID customerId, final Integer customerVersion, final CustomerData customerData) {
		final Customer customer = customerRepoDsl.getRepo().findLockedByIdOrFail(customerId);
		validateEntityVersion(customer.getVersion(), customerVersion);
		customer.setUpdatedAt(localDateTimeNow());
		BeanUtils.copyNonNullProperties(customerData, customer);
		final Customer updatedCustomer = customerRepoDsl.getRepo().saveAndFlush(customer);
		return customerMapper.map(updatedCustomer);
	}
}
