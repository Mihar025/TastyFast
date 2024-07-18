package com.misha.tastyfast.repositories;

import com.misha.tastyfast.model.Order;
import com.misha.tastyfast.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByIdAndUser(Integer id, User user);
    Page<Order> findAllByUser(User user, Pageable pageable);

}
