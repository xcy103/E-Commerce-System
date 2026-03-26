import React, { useState, useEffect } from 'react';
import { cartApi, orderApi } from '../services/api';
import type { CartResponse } from '../types';
import { useNavigate } from 'react-router-dom';

export const Cart: React.FC = () => {
  const [cart, setCart] = useState<CartResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [checkingOut, setCheckingOut] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    loadCart();
  }, []);

  const loadCart = async () => {
    try {
      setLoading(true);
      const data = await cartApi.getCart();
      setCart(data);
    } catch (error) {
      console.error('Failed to load cart:', error);
      alert('Failed to load cart');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateQuantity = async (cartItemId: number, newQuantity: number) => {
    if (newQuantity < 1) return;

    try {
      const updatedCart = await cartApi.updateCartItem(cartItemId, { quantity: newQuantity });
      setCart(updatedCart);
    } catch (error) {
      console.error('Failed to update cart item:', error);
      alert('Failed to update cart item');
    }
  };

  const handleRemoveItem = async (cartItemId: number) => {
    try {
      const updatedCart = await cartApi.removeCartItem(cartItemId);
      setCart(updatedCart);
    } catch (error) {
      console.error('Failed to remove cart item:', error);
      alert('Failed to remove cart item');
    }
  };

  const handleClearCart = async () => {
    if (!confirm('Are you sure you want to clear your cart?')) return;

    try {
      await cartApi.clearCart();
      await loadCart();
    } catch (error) {
      console.error('Failed to clear cart:', error);
      alert('Failed to clear cart');
    }
  };

  const handleCheckout = async () => {
    if (!cart || cart.items.length === 0) {
      alert('Your cart is empty');
      return;
    }

    try {
      setCheckingOut(true);
      const order = await orderApi.checkout();
      alert(`Order placed successfully! Order #${order.orderNumber}`);
      navigate('/orders');
      await loadCart();
    } catch (error: any) {
      console.error('Checkout failed:', error);
      const errorMessage = error.response?.data?.message || 'Failed to checkout';
      alert(errorMessage);
    } finally {
      setCheckingOut(false);
    }
  };

  if (loading) {
    return <div className="loading">Loading cart...</div>;
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="cart-container">
        <h2>Shopping Cart</h2>
        <div className="empty-cart">Your cart is empty</div>
      </div>
    );
  }

  return (
    <div className="cart-container">
      <div className="cart-header">
        <h2>Shopping Cart</h2>
        <button onClick={handleClearCart} className="clear-cart-btn">Clear Cart</button>
      </div>

      <div className="cart-items">
        {cart.items.map((item) => (
          <div key={item.cartItemId} className="cart-item">
            <div className="cart-item-info">
              <h3>{item.productName}</h3>
              <p className="cart-item-price">${item.productPrice.toFixed(2)} each</p>
            </div>
            <div className="cart-item-controls">
              <div className="quantity-controls">
                <button
                  onClick={() => handleUpdateQuantity(item.cartItemId, item.quantity - 1)}
                  className="quantity-btn"
                >
                  -
                </button>
                <span className="quantity">{item.quantity}</span>
                <button
                  onClick={() => handleUpdateQuantity(item.cartItemId, item.quantity + 1)}
                  className="quantity-btn"
                >
                  +
                </button>
              </div>
              <div className="cart-item-subtotal">${item.subtotal.toFixed(2)}</div>
              <button
                onClick={() => handleRemoveItem(item.cartItemId)}
                className="remove-item-btn"
              >
                Remove
              </button>
            </div>
          </div>
        ))}
      </div>

      <div className="cart-summary">
        <div className="cart-totals">
          <div className="total-items">Total Items: {cart.totalItems}</div>
          <div className="total-amount">Total: ${cart.totalAmount.toFixed(2)}</div>
        </div>
        <button
          onClick={handleCheckout}
          className="checkout-btn"
          disabled={checkingOut}
        >
          {checkingOut ? 'Processing...' : 'Checkout'}
        </button>
      </div>
    </div>
  );
};

