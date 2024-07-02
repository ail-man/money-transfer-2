package com.ail.home.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ail.home.transfer.persistence.AccountHistory;
import com.ail.home.transfer.persistence.CompositeHistoryId;

@Repository
public interface AccountHistoryRepo extends JpaRepository<AccountHistory, CompositeHistoryId> {
}
