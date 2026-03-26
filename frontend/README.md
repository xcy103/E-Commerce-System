# E-Commerce Frontend

React + TypeScript frontend for the E-Commerce System.

## Setup

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

The frontend will run on `http://localhost:3000` and will proxy API requests to the Spring Boot backend at `http://localhost:8080/api`.

## Build

To build for production:
```bash
npm run build
```

## Features

- **Product Listing**: Browse and search products
- **Shopping Cart**: Add, update, and remove items from cart
- **Order Management**: View order history and order details
- **Real-time Updates**: Cart count updates automatically

## API Integration

The frontend integrates with the following backend endpoints:

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search?query=...` - Search products
- `GET /api/cart` - Get cart
- `POST /api/cart/items` - Add item to cart
- `PUT /api/cart/items/{id}` - Update cart item
- `DELETE /api/cart/items/{id}` - Remove cart item
- `DELETE /api/cart` - Clear cart
- `POST /api/orders/checkout` - Checkout
- `GET /api/orders` - Get user orders
- `GET /api/orders/{id}` - Get order by ID

