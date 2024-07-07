package com.misha.tastyfast.model;

import com.misha.tastyfast.comon.BaseEntity;
import com.misha.tastyfast.transactionHistory.DishesTransactionHistory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_dishes")
@EntityListeners(AuditingEntityListener.class)
public class Dishes extends BaseEntity {

    private String dishesName;
    private String dishesOwner;
    private String dishesDescription;
    private double price;
    private BigDecimal calories;
    private String dishesCover;
    private boolean inStock;
    private String category;
    private boolean isVegetarian;


    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "dishes")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "dishes")
    private List<DishesTransactionHistory> histories;

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