package com.misha.tastyfast.model;

import com.misha.tastyfast.comon.BaseEntity;
import com.misha.tastyfast.transactionHistory.StoreTransactionHistory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_store")
@EntityListeners(AuditingEntityListener.class)
public class Store extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "rating")
    private Double rating;


    private boolean deliveryAvailable;



    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Product> products;
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Drink> drinks;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "logo_url")
    private byte[] logoUrl;

    @OneToMany(mappedBy = "store")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "store")
    private List<StoreTransactionHistory> transactionHistories;
}