package com.misha.tastyfast.repositories;

import com.misha.tastyfast.repositories.ProductTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducTransactionHistoryRepository extends JpaRepository<ProductTransactionHistory, Integer> {
    @Query(
            """
            select 
            (count (*) > 0) as isOrdered
            FROM ProductTransactionHistory  productTransactionHistory
            WHERE productTransactionHistory.user.id = :userId
            AND productTransactionHistory.product.id= :productId
            AND productTransactionHistory.alreadyOrdered = false 
    """
    )
    boolean isAlreadyOrderedByUser(@Param("productId") Integer productId, @Param("userId") Integer userId);
}
