package com.misha.tastyfast.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrinkTransactionHistoryRepository extends JpaRepository<DrinkTransactionHistory, Integer> {
}
