package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing shopping carts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    /**
     * Get or create cart for a user.
     */
    @Transactional
    public Cart getOrCreateCart(String userId) {
        log.debug("Getting or creating cart for user: {}", userId);

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Get cart by ID.
     */
    @Transactional(readOnly = true)
    public Cart getCartById(Long cartId) {
        log.debug("Fetching cart with ID: {}", cartId);
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
    }

    /**
     * Add item to cart.
     */
    @Transactional
    public Cart addItemToCart(String userId, Long productId, Integer quantity) {
        log.info("Adding product {} (quantity: {}) to cart for user: {}", productId, quantity, userId);

        Cart cart = getOrCreateCart(userId);
        Product product = productService.getProductById(productId);

        // Check if product has sufficient stock
        if (!product.hasStock(quantity)) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                            product.getName(), product.getStockQuantity(), quantity));
        }

        CartItem cartItem = new CartItem(product, quantity);
        cart.addItem(cartItem);

        return cartRepository.save(cart);
    }

    /**
     * Update cart item quantity.
     */
    @Transactional
    public Cart updateCartItemQuantity(String userId, Long cartItemId, Integer quantity) {
        log.info("Updating cart item {} quantity to {} for user: {}", cartItemId, quantity, userId);

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        // Check if product has sufficient stock for new quantity
        if (!cartItem.getProduct().hasStock(quantity)) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                            cartItem.getProduct().getName(), cartItem.getProduct().getStockQuantity(), quantity));
        }

        cartItem.setQuantity(quantity);

        return cartRepository.save(cart);
    }

    /**
     * Remove item from cart.
     */
    @Transactional
    public Cart removeItemFromCart(String userId, Long cartItemId) {
        log.info("Removing cart item {} for user: {}", cartItemId, userId);

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        cart.removeItem(cartItem);

        return cartRepository.save(cart);
    }

    /**
     * Clear all items from cart.
     */
    @Transactional
    public void clearCart(String userId) {
        log.info("Clearing cart for user: {}", userId);

        Cart cart = getOrCreateCart(userId);
        cart.clear();

        cartRepository.save(cart);
    }
}
