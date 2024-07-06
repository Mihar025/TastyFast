package com.misha.tastyfast.requests;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private Integer id;
    private String productName;
    private String productDescription;
    private double price;
    private BigDecimal calories;
    private String owner;
    private boolean inStock;
    private byte[] cover;
    private double rate;




}
