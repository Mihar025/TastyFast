package com.misha.tastyfast.Controllers;

import com.misha.tastyfast.model.Order;
import com.misha.tastyfast.requests.OrderRequest;
import com.misha.tastyfast.requests.OrderResponse;
import com.misha.tastyfast.requests.PageResponse;
import com.misha.tastyfast.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest, Authentication authentication) {
        OrderResponse createdOrder = orderService.createOrder(orderRequest, authentication);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer orderId, Authentication authentication) {
        Order order = orderService.getOrderById(orderId, authentication);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<OrderResponse> response = orderService.getUserOrders(page, size, authentication);
        return ResponseEntity.ok(response);
    }
}