package com.misha.tastyfast.repositories;

import com.misha.tastyfast.model.Dishes;
import com.misha.tastyfast.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DishesRepository extends JpaRepository<Dishes, Integer> , JpaSpecificationExecutor<Dishes> {

    @Query
            ("""
        SELECT  dishes
        FROM Dishes dishes
        WHERE dishes.inStock = true
            """)
    Page<Dishes> findAllDisplayableDishes(Pageable pageable, Integer id);



}
