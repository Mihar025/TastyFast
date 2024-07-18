package com.misha.tastyfast.requests.orderRequests;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data

public class OrderDetailsResponse {
    private Integer orderId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private Integer totalQuantity;
    private String orderType;
    private List<OrderItemResponse> items;

}
