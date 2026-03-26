package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.exception.InsufficientInventoryException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for managing orders with transactional checkout.
 * 
 * Key Features:
 * - ACID transactional checkout with pessimistic locking
 * - Atomic inventory deduction
 * - Order creation with product snapshots
 * - Automatic rollback on errors or insufficient inventory
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderFulfillmentService orderFulfillmentService;

    /**
     * Process checkout with ACID guarantees.
     * 
     * Transactional with SERIALIZABLE isolation ensures:
     * - Atomic inventory updates using pessimistic locking
     * - Consistent order creation
     * - Automatic rollback on failure
     * - Prevention of overselling in concurrent scenarios
     * 
     * @param userId User placing the order
     * @return Created order
     * @throws InsufficientInventoryException if any product has insufficient stock
     * @throws ResourceNotFoundException      if cart or products not found
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order checkout(String userId) {
        log.info("Starting checkout for user: {}", userId);

        // Get user's cart
        Cart cart = cartService.getOrCreateCart(userId);

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart");
        }

        // Create new order
        Order order = new Order();
        order.setOrderNumber(Order.generateOrderNumber());
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process each cart item with pessimistic locking
        for (CartItem cartItem : cart.getCartItems()) {
            Long productId = cartItem.getProduct().getId();
            Integer requestedQuantity = cartItem.getQuantity();

            // Acquire pessimistic write lock on product to prevent concurrent modifications
            Product product = productRepository.findByIdWithLock(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

            log.debug("Locked product {} for checkout. Current stock: {}",
                    product.getName(), product.getStockQuantity());

            // Check inventory availability
            if (!product.hasStock(requestedQuantity)) {
                log.warn("Insufficient inventory for product {}. Available: {}, Requested: {}",
                        product.getName(), product.getStockQuantity(), requestedQuantity);
                throw new InsufficientInventoryException(
                        product.getName(), product.getStockQuantity(), requestedQuantity);
            }

            // Reduce inventory atomically
            product.reduceStock(requestedQuantity);
            productRepository.save(product);

            log.debug("Reduced stock for product {}. New stock: {}",
                    product.getName(), product.getStockQuantity());

            // Create order item with product snapshot
            OrderItem orderItem = new OrderItem(product, requestedQuantity);
            order.addOrderItem(orderItem);

            // Calculate total
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        log.info("Order {} created successfully. Total: ${}",
                savedOrder.getOrderNumber(), savedOrder.getTotalAmount());

        // Clear cart
        cartService.clearCart(userId);

        // Submit order for asynchronous fulfillment after transaction commits
        // Ensures order is visible to background thread
        final Long orderId = savedOrder.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.debug("Transaction committed, submitting order {} for fulfillment", orderId);
                orderFulfillmentService.submitOrderForFulfillment(orderId);
            }
        });

        return savedOrder;
    }

    /**
     * Get order by ID.
     */
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        log.debug("Fetching order with ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    /**
     * Get order by order number.
     */
    @Transactional(readOnly = true)
    public Order getOrderByOrderNumber(String orderNumber) {
        log.debug("Fetching order with number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
    }

    /**
     * Get all orders for a user.
     */
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(String userId) {
        log.debug("Fetching orders for user: {}", userId);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get all orders for a user with pagination.
     */
    @Transactional(readOnly = true)
    public Page<Order> getUserOrders(String userId, Pageable pageable) {
        log.debug("Fetching orders for user {} with pagination: {}", userId, pageable);
        return orderRepository.findByUserId(userId, pageable);
    }

    /**
     * Update order status.
     * Used by the fulfillment service.
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order {} status to {}", orderId, newStatus);

        Order order = getOrderById(orderId);
        order.updateStatus(newStatus);

        return orderRepository.save(order);
    }

    /**
     * Get all orders by status.
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        log.debug("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(status);
    }
}
