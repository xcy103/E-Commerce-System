package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for order management.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    // Hardcoded user ID for simplicity
    // In production, retrieve from authentication context
    private static final String DEFAULT_USER_ID = "user123";

    @PostMapping("/checkout")
    @Operation(summary = "Checkout", description = "Process checkout and create an order from the cart")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout() {
        Order order = orderService.checkout(DEFAULT_USER_ID);
        OrderResponse response = mapToOrderResponse(order);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get user orders", description = "Retrieve all orders for the current user")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders() {
        List<Order> orders = orderService.getUserOrders(DEFAULT_USER_ID);

        List<OrderResponse> responses = orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        OrderResponse response = mapToOrderResponse(order);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by number", description = "Retrieve a specific order by its order number")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByOrderNumber(orderNumber);
        OrderResponse response = mapToOrderResponse(order);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Map Order entity to OrderResponse DTO.
     */
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());

        response.setItems(order.getOrderItems().stream()
                .map(this::mapToOrderItemDTO)
                .collect(Collectors.toList()));

        return response;
    }

    /**
     * Map OrderItem entity to OrderItemDTO.
     */
    private OrderResponse.OrderItemDTO mapToOrderItemDTO(OrderItem item) {
        return new OrderResponse.OrderItemDTO(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity(),
                item.getSubtotal());
    }
}
