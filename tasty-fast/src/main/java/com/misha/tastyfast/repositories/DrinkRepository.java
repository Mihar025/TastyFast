package com.misha.tastyfast.repositories;

import com.misha.tastyfast.model.Drinks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrinkRepository extends JpaRepository<Drinks, Integer> {




}
