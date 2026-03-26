package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for E-Commerce Order and Inventory Management System.
 * 
 * Features:
 * - REST API for products, cart, and order placement
 * - Real database persistence using JPA/Hibernate with PostgreSQL
 * - Transactional checkout with ACID guarantees
 * - Background worker thread for asynchronous order fulfillment
 */
@SpringBootApplication
public class ECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }
}
