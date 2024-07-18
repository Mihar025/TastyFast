package com.misha.tastyfast.requests.orderRequests;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class OrderItemResponse {

    private String itemType;
    private String itemName;
    private int quantity;
    private BigDecimal price;
}
