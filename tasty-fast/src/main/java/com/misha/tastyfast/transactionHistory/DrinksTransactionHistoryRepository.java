package com.misha.tastyfast.transactionHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrinksTransactionHistoryRepository extends JpaRepository<DrinksTransactionHistory, Integer> {
}
