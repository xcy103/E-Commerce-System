import React, { useState, useEffect } from 'react';
import { productApi, cartApi } from '../services/api';
import type { Product } from '../types';
import { ProductCard } from './ProductCard';

export const ProductList: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [cartItemCount, setCartItemCount] = useState(0);

  useEffect(() => {
    loadProducts();
    loadCart();
  }, []);

  const loadProducts = async () => {
    try {
      setLoading(true);
      const data = await productApi.getAllProducts();
      setProducts(data);
    } catch (error) {
      console.error('Failed to load products:', error);
      alert('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const loadCart = async () => {
    try {
      const cart = await cartApi.getCart();
      setCartItemCount(cart.totalItems);
    } catch (error) {
      console.error('Failed to load cart:', error);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      loadProducts();
      return;
    }

    try {
      setLoading(true);
      const data = await productApi.searchProducts(searchQuery);
      setProducts(data);
    } catch (error) {
      console.error('Failed to search products:', error);
      alert('Failed to search products');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async () => {
    await loadCart();
  };

  if (loading) {
    return <div className="loading">Loading products...</div>;
  }

  return (
    <div className="product-list-container">
      <div className="search-bar">
        <input
          type="text"
          placeholder="Search products..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          className="search-input"
        />
        <button onClick={handleSearch} className="search-btn">Search</button>
        <button onClick={loadProducts} className="clear-search-btn">Clear</button>
      </div>

      <div className="products-grid">
        {products.length === 0 ? (
          <div className="no-products">No products found</div>
        ) : (
          products.map((product) => (
            <ProductCard key={product.id} product={product} onAddToCart={handleAddToCart} />
          ))
        )}
      </div>
    </div>
  );
};

