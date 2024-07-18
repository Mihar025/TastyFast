package com.misha.tastyfast.services;


import com.misha.tastyfast.mapping.OrderMapper;
import com.misha.tastyfast.model.*;
import com.misha.tastyfast.repositories.OrderRepository;
import com.misha.tastyfast.repositories.RestaurantRepository;
import com.misha.tastyfast.repositories.StoreRepository;
import com.misha.tastyfast.requests.cartRequests.CartItemResponse;
import com.misha.tastyfast.requests.cartRequests.CartResponse;
import com.misha.tastyfast.requests.dishesRequests.DishesResponse;
import com.misha.tastyfast.requests.drinkRequests.DrinksResponse;
import com.misha.tastyfast.requests.orderRequests.*;
import com.misha.tastyfast.requests.pageResponse.PageResponse;
import com.misha.tastyfast.requests.productRequests.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final DishesService dishesService;
    private final DrinkService drinkService;
    private final OrderMapper orderMapper;
    private final RestaurantRepository restaurantRepository;
    private final StoreRepository storeRepository;
    private final CartService cartService;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        if ("RESTAURANT".equals(orderRequest.getOrderType())) {
            Restaurant restaurant = restaurantRepository.findById(orderRequest.getSourceId())
                    .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
            order.setRestaurant(restaurant);
        } else if ("STORE".equals(orderRequest.getOrderType())) {
            Store store = storeRepository.findById(orderRequest.getSourceId())
                    .orElseThrow(() -> new EntityNotFoundException("Store not found"));
            order.setStore(store);
        } else {
            throw new IllegalArgumentException("Invalid order type");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        Integer totalQuantity = 0;
        log.info("Creating order for user: {}, order type: {}", user.getId(), orderRequest.getOrderType());

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            OrderItem orderItem = createOrderItem(itemRequest, order, authentication);
            order.getOrderItems().add(orderItem);
            orderItem.setOrder(order);
            totalAmount = totalAmount.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            totalQuantity += itemRequest.getQuantity();
            log.info("Added order item: type={}, id={}, quantity={}", itemRequest.getItemType(), itemRequest.getItemId(), itemRequest.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        order.setQuantity(totalQuantity);
        log.info("Saving order with {} items, total amount: {}", order.getOrderItems().size(), totalAmount);
        Order savedOrder = orderRepository.save(order);
        log.info("Created new order with ID: {} for user: {}", savedOrder.getId(), user.getId());
        return orderMapper.mapToOrderResponse(savedOrder);
    }

    private OrderItem createOrderItem(OrderItemRequest itemRequest, Order order, Authentication authentication) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setFromWhere(order.getRestaurant() != null ? "RESTAURANT" : "STORE");
        orderItem.setQuantity(itemRequest.getQuantity());
        switch (itemRequest.getItemType()) {
            case "PRODUCT":
                ProductResponse product = productService.findProductById(itemRequest.getItemId(), authentication);
                orderItem.setItemName(product.getProductName());
                orderItem.setPrice(product.getPrice());
                orderItem.setItemType("PRODUCT");
                break;
            case "DISH":
                DishesResponse dish = dishesService.findDishesById(itemRequest.getItemId(), authentication);
                orderItem.setItemName(dish.getDishesName());
                orderItem.setPrice(dish.getPrice());
                orderItem.setItemType("DISH");
                break;
            case "DRINK":
                DrinksResponse drink = drinkService.findDrinkById(itemRequest.getItemId(), authentication);
                orderItem.setItemName(drink.getDrinkName());
                orderItem.setPrice(drink.getPrice());
                orderItem.setItemType("DRINK");
                break;
            default:
                throw new IllegalArgumentException("Invalid item type: " + itemRequest.getItemType());
        }

        return orderItem;
    }

    @Cacheable(value = "orders", key = "#orderId + '_' + #authentication.name")
    public Order getOrderById(Integer orderId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
    }

    @Cacheable(value = "userOrders", key = "#authentication.name")
    public PageResponse<OrderResponse> getUserOrders(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orders = orderRepository.findAllByUser(user, pageable);

        List<OrderResponse> orderResponses = orders.getContent().stream()
                .map(orderMapper::mapToOrderResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                orderResponses,
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isFirst(),
                orders.isLast()
        );
    }


    @Transactional
    public OrderResponse createOrderFromCart (User user){
        CartResponse cartResponse = cartService.getCartForUser(user);
        if(cartResponse.getItems().isEmpty()){
            throw new IllegalStateException("Cannot create order from empty cart!");
        }
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemResponse cartItemResponse : cartResponse.getItems()) {
            OrderItem orderItem = cartService.createOrderItemFromCartItem(cartItemResponse, order);
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(user);
        return orderMapper.mapToOrderResponse(savedOrder);
    }


    public OrderDetailsResponse getOrderDetails(Integer orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Cannot find order with provided id! "+ orderId));

        OrderDetailsResponse orderDetailsResponse = new OrderDetailsResponse();
        orderDetailsResponse.setOrderId(order.getId());
        orderDetailsResponse.setOrderDate(order.getOrderDate());
        orderDetailsResponse.setStatus(order.getStatus());
        orderDetailsResponse.setTotalAmount(order.getTotalAmount());
        orderDetailsResponse.setTotalQuantity(order.getQuantity());
        orderDetailsResponse.setOrderType(order.getRestaurant() != null ? "RESTAURANT" : "STORE");

        List<OrderItemResponse> items = order.getOrderItems().stream().map(orderMapper::mapToOrderItemResponse).collect(Collectors.toList());
        orderDetailsResponse.setItems(items);
        return orderDetailsResponse;


    }





}
