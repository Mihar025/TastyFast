package com.misha.tastyfast.requests.cartRequests;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse  {
    private Integer id;
    private String itemType;
    private Integer itemId;
    private String itemName;
    private int quantity;
    private BigDecimal price;


}
