package com.ecommerce.entity;

/**
 * Enum representing the status of an order through its lifecycle.
 */
public enum OrderStatus {
    /**
     * Order has been created but not yet processed.
     */
    PENDING,

    /**
     * Order is being processed by the fulfillment worker.
     */
    PROCESSING,

    /**
     * Order has been successfully fulfilled.
     */
    FULFILLED,

    /**
     * Order has been cancelled.
     */
    CANCELLED
}
