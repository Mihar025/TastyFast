package com.misha.tastyfast.transactionHistory;

import com.misha.tastyfast.transactionHistory.DishesTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishesTransactionRepository extends JpaRepository<DishesTransactionHistory, Integer> {

}
