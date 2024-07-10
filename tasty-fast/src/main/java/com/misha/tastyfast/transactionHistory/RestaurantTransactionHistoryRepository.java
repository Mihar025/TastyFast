package com.misha.tastyfast.transactionHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantTransactionHistoryRepository extends JpaRepository<RestaurantTransactionHistory, Integer> {
}
