package com.misha.tastyfast.model;

import com.misha.tastyfast.comon.BaseEntity;
import com.misha.tastyfast.transactionHistory.ProductTransactionHistory;
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
@Table(name = "_product")
@EntityListeners(AuditingEntityListener.class)
public class Product extends BaseEntity {

    private String productName;
    private String productOwner;
    private String productDescription;
    private double price;
    private BigDecimal calories;
    private String productCover;
    private boolean inStock;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "product")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "product")
    private List<ProductTransactionHistory> histories;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

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