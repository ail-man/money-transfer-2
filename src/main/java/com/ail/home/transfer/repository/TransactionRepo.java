package com.ail.home.transfer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ail.home.transfer.exceptions.EntityNotFoundException;
import com.ail.home.transfer.persistence.Transaction;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, UUID> {

	default Transaction findByIdOrFail(final UUID id) throws EntityNotFoundException {
		return this.findById(id)
			.orElseThrow(() -> EntityNotFoundException.accountIdNotFound(id.toString()));
	}
}
