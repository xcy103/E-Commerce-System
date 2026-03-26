package com.ecommerce.controller;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.CartResponse;
import com.ecommerce.dto.UpdateCartItemRequest;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * REST controller for shopping cart management.
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management APIs")
public class CartController {

    private final CartService cartService;

    // Hardcoded user ID for simplicity
    // In production, retrieve from authentication context
    private static final String DEFAULT_USER_ID = "user123";

    @GetMapping
    @Operation(summary = "Get cart", description = "Retrieve the current user's shopping cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        Cart cart = cartService.getOrCreateCart(DEFAULT_USER_ID);
        CartResponse response = mapToCartResponse(cart);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Add a product to the shopping cart")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @Valid @RequestBody AddToCartRequest request) {

        Cart cart = cartService.addItemToCart(
                DEFAULT_USER_ID,
                request.getProductId(),
                request.getQuantity());

        CartResponse response = mapToCartResponse(cart);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", response));
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update cart item", description = "Update the quantity of a cart item")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        Cart cart = cartService.updateCartItemQuantity(
                DEFAULT_USER_ID,
                cartItemId,
                request.getQuantity());

        CartResponse response = mapToCartResponse(cart);
        return ResponseEntity.ok(ApiResponse.success("Cart item updated", response));
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Remove cart item", description = "Remove an item from the shopping cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(@PathVariable Long cartItemId) {
        Cart cart = cartService.removeItemFromCart(DEFAULT_USER_ID, cartItemId);
        CartResponse response = mapToCartResponse(cart);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", response));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Remove all items from the shopping cart")
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        cartService.clearCart(DEFAULT_USER_ID);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }

    /**
     * Map Cart entity to CartResponse DTO.
     */
    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setTotalAmount(cart.calculateTotal());
        response.setTotalItems(cart.getTotalItems());

        response.setItems(cart.getCartItems().stream()
                .map(this::mapToCartItemDTO)
                .collect(Collectors.toList()));

        return response;
    }

    /**
     * Map CartItem entity to CartItemDTO.
     */
    private CartResponse.CartItemDTO mapToCartItemDTO(CartItem item) {
        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartResponse.CartItemDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                subtotal);
    }
}
