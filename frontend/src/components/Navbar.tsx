import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { cartApi } from '../services/api';

export const Navbar: React.FC = () => {
  const location = useLocation();
  const [cartItemCount, setCartItemCount] = useState(0);

  useEffect(() => {
    loadCartCount();
    const interval = setInterval(loadCartCount, 2000); // Refresh every 2 seconds
    return () => clearInterval(interval);
  }, [location]);

  const loadCartCount = async () => {
    try {
      const cart = await cartApi.getCart();
      setCartItemCount(cart.totalItems);
    } catch (error) {
      console.error('Failed to load cart count:', error);
    }
  };

  return (
    <nav className="navbar">
      <div className="navbar-content">
        <Link to="/" className="navbar-brand">
          <h1>🛍️ E-Commerce</h1>
        </Link>
        <div className="navbar-links">
          <Link to="/" className={location.pathname === '/' ? 'active' : ''}>
            Products
          </Link>
          <Link to="/cart" className={location.pathname === '/cart' ? 'active' : ''}>
            Cart {cartItemCount > 0 && <span className="cart-badge">{cartItemCount}</span>}
          </Link>
          <Link to="/orders" className={location.pathname === '/orders' ? 'active' : ''}>
            Orders
          </Link>
        </div>
      </div>
    </nav>
  );
};

