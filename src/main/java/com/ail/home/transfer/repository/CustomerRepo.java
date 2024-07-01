package com.ail.home.transfer.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.ail.home.transfer.exceptions.EntityNotFoundException;
import com.ail.home.transfer.persistence.Customer;

import jakarta.persistence.LockModeType;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, UUID> {

	default Customer findLockedByIdOrFail(final UUID id) throws EntityNotFoundException {
		return this.findLockedById(id)
			.orElseThrow(() -> EntityNotFoundException.customerIdNotFound(id.toString()));
	}

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Customer> findLockedById(final UUID id);
}
