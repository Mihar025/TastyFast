package com.misha.tastyfast.transactionHistory;

import com.misha.tastyfast.transactionHistory.ProductTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTransactionHistoryRepository extends JpaRepository<ProductTransactionHistory, Integer> {
    @Query(
            """
            select 
            (count (*) > 0) as isOrdered
            FROM ProductTransactionHistory  productTransactionHistory
            WHERE productTransactionHistory.user.id = :userId
            AND productTransactionHistory.product.id= :productId
           
    """
    )
    boolean isAlreadyOrderedByUser(@Param("productId") Integer productId, @Param("userId") Integer userId);
}
