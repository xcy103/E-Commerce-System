import React, { useState, useEffect } from 'react';
import { orderApi } from '../services/api';
import type { OrderResponse } from '../types';

export const Orders: React.FC = () => {
  const [orders, setOrders] = useState<OrderResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      setLoading(true);
      const data = await orderApi.getUserOrders();
      setOrders(data);
    } catch (error) {
      console.error('Failed to load orders:', error);
      alert('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'FULFILLED':
        return 'status-fulfilled';
      case 'PROCESSING':
        return 'status-processing';
      case 'PENDING':
        return 'status-pending';
      case 'CANCELLED':
        return 'status-cancelled';
      default:
        return '';
    }
  };

  if (loading) {
    return <div className="loading">Loading orders...</div>;
  }

  if (orders.length === 0) {
    return (
      <div className="orders-container">
        <h2>My Orders</h2>
        <div className="no-orders">You have no orders yet</div>
      </div>
    );
  }

  return (
    <div className="orders-container">
      <h2>My Orders</h2>
      <div className="orders-list">
        {orders.map((order) => (
          <div key={order.orderId} className="order-card">
            <div className="order-header">
              <div>
                <h3>Order #{order.orderNumber}</h3>
                <p className="order-date">
                  {new Date(order.createdAt).toLocaleString()}
                </p>
              </div>
              <div className="order-status-info">
                <span className={`order-status ${getStatusColor(order.status)}`}>
                  {order.status}
                </span>
                <span className="order-total">${order.totalAmount.toFixed(2)}</span>
              </div>
            </div>
            <div className="order-items">
              {order.items.map((item) => (
                <div key={item.orderItemId} className="order-item">
                  <span className="order-item-name">{item.productName}</span>
                  <span className="order-item-quantity">Qty: {item.quantity}</span>
                  <span className="order-item-subtotal">${item.subtotal.toFixed(2)}</span>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

