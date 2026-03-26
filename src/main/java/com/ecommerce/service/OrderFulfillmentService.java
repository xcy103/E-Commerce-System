package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.repository.OrderRepository;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Background worker service for asynchronous order fulfillment.
 * 
 * Simulates real-world distributed systems where order processing
 * happens asynchronously after checkout.
 * 
 * Features:
 * - Thread pool for concurrent order processing
 * - Simulated processing delay
 * - Order status transitions (PENDING → PROCESSING → FULFILLED)
 * - Graceful shutdown handling
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderFulfillmentService {

    private final OrderRepository orderRepository;
    private final PlatformTransactionManager transactionManager;

    @Value("${app.order-fulfillment.thread-pool-size:5}")
    private int threadPoolSize;

    @Value("${app.order-fulfillment.processing-delay-seconds:10}")
    private int processingDelaySeconds;

    private ExecutorService executorService;
    private TransactionTemplate transactionTemplate;

    /**
     * Initialize the thread pool on service startup.
     */
    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        transactionTemplate = new TransactionTemplate(transactionManager);
        // REPEATABLE_READ isolation prevents phantom reads with pessimistic locking
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        log.info("OrderFulfillmentService initialized with thread pool size: {}", threadPoolSize);
    }

    /**
     * Submit an order for asynchronous fulfillment.
     * 
     * @param orderId ID of the order to fulfill
     */
    public void submitOrderForFulfillment(Long orderId) {
        log.info("Submitting order {} for asynchronous fulfillment", orderId);

        if (executorService == null) {
            log.error("ExecutorService is not initialized! Order {} cannot be processed. " +
                    "This may happen if submitOrderForFulfillment is called before @PostConstruct.", orderId);
            // Try to initialize if not already done
            init();
        }

        try {
            executorService.submit(() -> {
                try {
                    processOrder(orderId);
                } catch (Exception e) {
                    log.error("Unexpected error in fulfillment thread for order {}", orderId, e);
                }
            });
            log.debug("Order {} submitted to executor service", orderId);
        } catch (Exception e) {
            log.error("Failed to submit order {} for fulfillment", orderId, e);
        }
    }

    /**
     * Process an order asynchronously.
     * Simulates order fulfillment workflow.
     * 
     * @param orderId ID of the order to process
     */
    private void processOrder(Long orderId) {
        try {
            log.info("Starting fulfillment for order {}", orderId);

            if (transactionTemplate == null) {
                log.error("TransactionTemplate is not initialized! Cannot process order {}", orderId);
                return;
            }

            // Update status to PROCESSING with pessimistic locking to prevent concurrent processing
            transactionTemplate.execute(status -> {
                Order order = orderRepository.findByIdWithLock(orderId).orElse(null);
                if (order == null) {
                    log.error("Order {} not found", orderId);
                    return null;
                }

            // Process only PENDING orders to prevent duplicate processing
            // Pessimistic lock ensures single-threaded status check and update
                if (order.getStatus() != OrderStatus.PENDING) {
                    log.warn("Order {} is not in PENDING status (current: {}), skipping fulfillment", 
                            orderId, order.getStatus());
                    return null;
                }

                order.updateStatus(OrderStatus.PROCESSING);
                orderRepository.save(order);
                log.info("Order {} status updated to PROCESSING", orderId);
                return order;
            });

            // Simulate processing time (warehouse picking, packing, shipping)
            Thread.sleep(processingDelaySeconds * 1000L);

            // Update status to FULFILLED
            transactionTemplate.execute(status -> {
                Order orderToFulfill = orderRepository.findById(orderId).orElse(null);
                if (orderToFulfill == null) {
                    log.error("Order {} not found after processing delay", orderId);
                    return null;
                }

                // Verify order is still in PROCESSING status
                // Status may have changed during processing delay
                if (orderToFulfill.getStatus() != OrderStatus.PROCESSING) {
                    log.warn("Order {} status changed during processing (current: {}), skipping fulfillment", 
                            orderId, orderToFulfill.getStatus());
                    return null;
                }

                // Update status to FULFILLED
                orderToFulfill.updateStatus(OrderStatus.FULFILLED);
                orderRepository.save(orderToFulfill);
                log.info("Order {} successfully fulfilled", orderId);
                return orderToFulfill;
            });

        } catch (InterruptedException e) {
            log.error("Order {} fulfillment interrupted", orderId, e);
            Thread.currentThread().interrupt();

            // Update status to CANCELLED on failure
            try {
                transactionTemplate.execute(status -> {
                    Order order = orderRepository.findById(orderId).orElse(null);
                    if (order != null) {
                        order.updateStatus(OrderStatus.CANCELLED);
                        orderRepository.save(order);
                    }
                    return null;
                });
            } catch (Exception ex) {
                log.error("Failed to update order {} status to CANCELLED", orderId, ex);
            }

        } catch (Exception e) {
            log.error("Error processing order {}", orderId, e);

            // Retry logic can be implemented here for production use
            try {
                transactionTemplate.execute(status -> {
                    Order order = orderRepository.findById(orderId).orElse(null);
                    if (order != null) {
                        order.updateStatus(OrderStatus.CANCELLED);
                        orderRepository.save(order);
                    }
                    return null;
                });
            } catch (Exception ex) {
                log.error("Failed to update order {} status to CANCELLED", orderId, ex);
            }
        }
    }

    /**
     * Gracefully shutdown the executor service.
     * Waits for currently executing tasks to complete.
     */
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down OrderFulfillmentService...");

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("Executor did not terminate in time, forcing shutdown");
                executorService.shutdownNow();

                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("Executor did not terminate");
                }
            }
            log.info("OrderFulfillmentService shutdown complete");
        } catch (InterruptedException e) {
            log.error("Shutdown interrupted", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
