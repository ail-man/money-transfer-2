package com.ail.home.transfer.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.ail.home.transfer.exceptions.EntityNotFoundException;
import com.ail.home.transfer.persistence.Account;

import jakarta.persistence.LockModeType;

@Repository
public interface AccountRepo extends JpaRepository<Account, UUID> {

	default Account findLockedByIdOrFail(final UUID id) throws EntityNotFoundException {
		return this.findLockedById(id)
			.orElseThrow(() -> EntityNotFoundException.accountIdNotFound(id.toString()));
	}

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Account> findLockedById(final UUID id);
}
