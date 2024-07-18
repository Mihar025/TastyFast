package com.misha.tastyfast.mapping;

import com.misha.tastyfast.model.Order;
import com.misha.tastyfast.model.OrderItem;
import com.misha.tastyfast.requests.orderRequests.OrderDetailsResponse;
import com.misha.tastyfast.requests.orderRequests.OrderItemResponse;
import com.misha.tastyfast.requests.orderRequests.OrderResponse;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OrderMapper {


    public OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())  // Предполагая, что у Order поле называется restaurantId, а не orderId
                .orderDate(order.getOrderDate())  // Используйте дату заказа из Order, а не текущее время
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(order.getOrderItems().stream()
                        .map(this::mapToOrderItemResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .itemType(orderItem.getItemType())
                .itemName(orderItem.getItemName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }



}
