package com.misha.tastyfast.repositories;

import com.misha.tastyfast.repositories.ProductTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducTransactionHistoryRepository extends JpaRepository<ProductTransactionHistory, Integer> {
}
