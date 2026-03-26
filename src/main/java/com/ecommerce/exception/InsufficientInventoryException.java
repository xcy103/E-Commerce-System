package com.ecommerce.exception;

/**
 * Exception thrown when there is insufficient inventory to fulfill an order.
 */
public class InsufficientInventoryException extends RuntimeException {

    public InsufficientInventoryException(String message) {
        super(message);
    }

    public InsufficientInventoryException(String productName, int available, int requested) {
        super(String.format("Insufficient inventory for product '%s'. Available: %d, Requested: %d",
                productName, available, requested));
    }
}
