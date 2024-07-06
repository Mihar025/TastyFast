package com.misha.tastyfast.repositories;

import com.misha.tastyfast.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> , JpaSpecificationExecutor<Product> {
    @Query("""
        SELECT product
        FROM Product  product
        WHERE product.inStock = true 
""")
    Page<Product> findAllDisplayableBooks(Pageable pageable, Integer id);
}
