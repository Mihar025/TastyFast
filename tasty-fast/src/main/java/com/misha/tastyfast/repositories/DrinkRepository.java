package com.misha.tastyfast.repositories;

import com.misha.tastyfast.model.Dishes;
import com.misha.tastyfast.model.Drink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    @Query("SELECT d FROM Drink d WHERE d.restaurant.id = :restaurantId")
    Page<Drink> findAllDrinksInRestaurant(Pageable pageable, @Param("restaurantId") Integer restaurantId);


    Optional<Drink> findByIdAndRestaurantId(Integer drinkId, Integer restaurantId);

    @Query("SELECT d FROM Drink d WHERE d.store.id = :storeId")
    Page<Drink> findAllDrinksInStore(Pageable pageable, @Param("storeId") Integer storeId);


    Optional<Drink> findByIdAndStoreId(Integer drinkId, Integer storeId);
}
