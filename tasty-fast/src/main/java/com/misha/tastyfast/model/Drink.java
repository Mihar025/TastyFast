package com.misha.tastyfast.model;

import com.misha.tastyfast.comon.BaseEntity;
import com.misha.tastyfast.transactionHistory.DrinksTransactionHistory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_drinks")
public class Drink extends BaseEntity {

    private String drinksName;
    private String drinksOwner;
    private String drinksDescription;
    private double price;
    private BigDecimal calories;
    private String drinksCover;
    private boolean inStock;
    private String category;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "drink")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "drink")
    private List<DrinksTransactionHistory> histories;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);

        return Math.round(rate * 10.0) / 10.0;
    }
}