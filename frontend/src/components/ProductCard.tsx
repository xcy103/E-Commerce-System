import React from 'react';
import type { Product } from '../types';
import { cartApi } from '../services/api';

interface ProductCardProps {
  product: Product;
  onAddToCart: () => void;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, onAddToCart }) => {
  const handleAddToCart = async () => {
    try {
      await cartApi.addToCart({ productId: product.id, quantity: 1 });
      onAddToCart();
    } catch (error) {
      console.error('Failed to add to cart:', error);
      alert('Failed to add product to cart');
    }
  };

  return (
    <div className="product-card">
      <div className="product-card-content">
        <h3 className="product-name">{product.name}</h3>
        <p className="product-description">{product.description || 'No description available'}</p>
        <div className="product-footer">
          <div className="product-price-stock">
            <span className="product-price">${product.price.toFixed(2)}</span>
            <span className={`product-stock ${product.stockQuantity > 0 ? 'in-stock' : 'out-of-stock'}`}>
              {product.stockQuantity > 0 ? `${product.stockQuantity} in stock` : 'Out of stock'}
            </span>
          </div>
          <button
            className="add-to-cart-btn"
            onClick={handleAddToCart}
            disabled={product.stockQuantity === 0}
          >
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  );
};

