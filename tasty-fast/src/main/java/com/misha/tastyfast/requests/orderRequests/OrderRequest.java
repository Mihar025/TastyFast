package com.misha.tastyfast.requests.orderRequests;

import com.misha.tastyfast.requests.orderRequests.OrderItemRequest;
import lombok.Data;

import java.util.List;
@Data
public class OrderRequest {
    private List<OrderItemRequest> items;
    private String orderType; // "RESTAURANT" или "STORE"
    private Integer sourceId; // ID ресторана или магазина
}