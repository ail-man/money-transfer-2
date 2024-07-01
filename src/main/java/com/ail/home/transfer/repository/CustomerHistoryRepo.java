package com.ail.home.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ail.home.transfer.persistence.CompositeHistoryId;
import com.ail.home.transfer.persistence.CustomerHistory;

@Repository
public interface CustomerHistoryRepo extends JpaRepository<CustomerHistory, CompositeHistoryId> {
}
