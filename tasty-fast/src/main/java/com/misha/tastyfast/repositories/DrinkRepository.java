package com.misha.tastyfast.repositories;

import com.misha.tastyfast.model.Drink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Integer>, JpaSpecificationExecutor<Drink>{
    @Query
            (
                    """
                    SELECT drink
                    FROM Drink  drink
                    WHERE drink.inStock = true
                  
            """
            )
    Page<Drink> findAllDisplayableDrinks(Pageable pageable, Integer id);


}
