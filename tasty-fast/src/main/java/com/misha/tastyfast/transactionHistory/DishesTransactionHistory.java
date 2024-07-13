package com.misha.tastyfast.transactionHistory;

import com.misha.tastyfast.comon.BaseEntity;
import com.misha.tastyfast.model.Dishes;
import com.misha.tastyfast.model.Restaurant;
import com.misha.tastyfast.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DishesTransactionHistory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "dishes_id")
    private Dishes dishes;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private String transactionType;
    private LocalDateTime transactionDate;
    private boolean already_ordered;
    private boolean returned;
    private boolean already_returned;
    private BigDecimal amount;
    private String details;
}