package com.ecommerce.config;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Initialize database with sample product data.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // Only initialize if database is empty
        if (productRepository.count() == 0) {
            log.info("Initializing database with sample products...");

            List<Product> products = Arrays.asList(
                    createProduct("Modern Sofa", "3-seater fabric sofa with comfortable cushions",
                            new BigDecimal("899.99"), 55),
                    createProduct("Coffee Table", "Oak wood coffee table with storage shelf",
                            new BigDecimal("249.99"), 25),
                    createProduct("Dining Table", "6-person extendable dining table in walnut finish",
                            new BigDecimal("699.99"), 32),
                    createProduct("Queen Bed Frame", "Platform bed frame with upholstered headboard",
                            new BigDecimal("599.99"), 68),
                    createProduct("Office Chair", "Ergonomic office chair with lumbar support",
                            new BigDecimal("349.99"), 30),
                    createProduct("Bookshelf", "5-tier wooden bookshelf with adjustable shelves",
                            new BigDecimal("179.99"), 22),
                    createProduct("Dresser", "6-drawer dresser with mirror in white finish",
                            new BigDecimal("449.99"), 10),
                    createProduct("Nightstand", "Bedside nightstand with drawer and shelf",
                            new BigDecimal("129.99"), 35),
                    createProduct("Wardrobe", "3-door wardrobe with sliding doors and interior lighting",
                            new BigDecimal("799.99"), 50),
                    createProduct("Dining Chair", "Set of 4 upholstered dining chairs",
                            new BigDecimal("299.99"), 20));

            productRepository.saveAll(products);
            log.info("Database initialized with {} products", products.size());
        } else {
            log.info("Database already contains products, skipping initialization");
        }
    }

    private Product createProduct(String name, String description, BigDecimal price, int stock) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stock);
        return product;
    }
}
