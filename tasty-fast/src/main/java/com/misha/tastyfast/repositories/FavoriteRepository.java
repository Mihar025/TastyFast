package com.misha.tastyfast.repositories;

import com.misha.tastyfast.model.Favorite;
import com.misha.tastyfast.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findAllByUser(User user);
}
