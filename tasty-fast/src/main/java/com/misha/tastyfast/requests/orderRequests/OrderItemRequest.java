package com.misha.tastyfast.requests.orderRequests;

import lombok.Data;

@Data
public class OrderItemRequest {
    private String itemType; // "PRODUCT", "DISH", или "DRINK"
    private Integer itemId;
    private int quantity;
}