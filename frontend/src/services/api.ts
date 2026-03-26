import axios from 'axios';
import type { 
  Product, 
  CartResponse, 
  AddToCartRequest, 
  UpdateCartItemRequest,
  OrderResponse,
  ApiResponse 
} from '../types';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Product API
export const productApi = {
  getAllProducts: async (): Promise<Product[]> => {
    const response = await api.get<ApiResponse<Product[]>>('/products');
    return response.data.data;
  },

  getProductById: async (id: number): Promise<Product> => {
    const response = await api.get<ApiResponse<Product>>(`/products/${id}`);
    return response.data.data;
  },

  searchProducts: async (query: string): Promise<Product[]> => {
    const response = await api.get<ApiResponse<Product[]>>('/products/search', {
      params: { query },
    });
    return response.data.data;
  },
};

// Cart API
export const cartApi = {
  getCart: async (): Promise<CartResponse> => {
    const response = await api.get<ApiResponse<CartResponse>>('/cart');
    return response.data.data;
  },

  addToCart: async (request: AddToCartRequest): Promise<CartResponse> => {
    const response = await api.post<ApiResponse<CartResponse>>('/cart/items', request);
    return response.data.data;
  },

  updateCartItem: async (cartItemId: number, request: UpdateCartItemRequest): Promise<CartResponse> => {
    const response = await api.put<ApiResponse<CartResponse>>(`/cart/items/${cartItemId}`, request);
    return response.data.data;
  },

  removeCartItem: async (cartItemId: number): Promise<CartResponse> => {
    const response = await api.delete<ApiResponse<CartResponse>>(`/cart/items/${cartItemId}`);
    return response.data.data;
  },

  clearCart: async (): Promise<void> => {
    await api.delete<ApiResponse<void>>('/cart');
  },
};

// Order API
export const orderApi = {
  checkout: async (): Promise<OrderResponse> => {
    const response = await api.post<ApiResponse<OrderResponse>>('/orders/checkout');
    return response.data.data;
  },

  getUserOrders: async (): Promise<OrderResponse[]> => {
    const response = await api.get<ApiResponse<OrderResponse[]>>('/orders');
    return response.data.data;
  },

  getOrderById: async (orderId: number): Promise<OrderResponse> => {
    const response = await api.get<ApiResponse<OrderResponse>>(`/orders/${orderId}`);
    return response.data.data;
  },
};

