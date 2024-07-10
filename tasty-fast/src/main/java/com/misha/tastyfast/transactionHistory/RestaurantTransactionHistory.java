package com.misha.tastyfast.transactionHistory;

import com.misha.tastyfast.comon.BaseEntity;
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
public class RestaurantTransactionHistory extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String transactionType;
    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private String details;
}