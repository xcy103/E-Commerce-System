# E-Commerce Order and Inventory Management System

A production-grade backend E-Commerce system built with Spring Boot, featuring transactional checkout with ACID guarantees and asynchronous order fulfillment.

## 🚀 Quick Start

### Prerequisites (Use check-requirements.sh to verify)
- **Homebrew** - Package manager for macOS (install from https://brew.sh)
- **Java 8** - Install via `brew install java8`
- **Maven** - Install via `./install-maven.sh` or `brew install maven`
- **PostgreSQL** - Install via `brew install postgresql@14` and start with `brew services start postgresql@14`
- **Node.js** - Install via `brew install node` (includes npm)

### Database Setup
```bash
# Create database
psql postgres
CREATE DATABASE ecommerce_db;
\q
```

### Run the Application

**Option 1: Using provided scripts (Recommended for macOS. If errors, try option 2.)**
```bash
# Build the project
./build.sh

# Run the application
./run.sh
```

**Option 2: Using Maven directly (Ensure Java 8 is set as the default Java version)**
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The application will start at `http://localhost:8080/api`

### Frontend Setup

The project includes a React + TypeScript frontend located in the `frontend/` directory.

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will run on `http://localhost:3000` and automatically proxy API requests to the backend.

## 📚 API Documentation

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **API Docs**: http://localhost:8080/api/api-docs

## 🔌 Key API Endpoints

### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search?query={term}` - Search products

### Shopping Cart
- `GET /api/cart` - Get current cart
- `POST /api/cart/items` - Add item to cart
- `PUT /api/cart/items/{itemId}` - Update item quantity
- `DELETE /api/cart/items/{itemId}` - Remove item from cart

### Orders
- `POST /api/orders/checkout` - Process checkout (creates order)
- `GET /api/orders` - Get user's orders
- `GET /api/orders/{id}` - Get order by ID

## 🎯 Key Features

### Backend
- **REST API** - Full CRUD operations for products, cart, and orders
- **Database** - Database persistence using ORM (JPA/Hibernate) with PostgreSQL.
- **ACID Transactions** - SERIALIZABLE isolation for checkout
- **Pessimistic Locking** - Prevents overselling in concurrent scenarios
- **Async Order Fulfillment** - Background worker processes orders (PENDING → PROCESSING → FULFILLED)
- **Thread-Safe** - Concurrent order processing with proper locking

### Frontend
- **Product Browsing** - Browse and search products with real-time updates
- **Shopping Cart** - Add, update, and remove items with quantity management
- **Order Management** - View order history and track order status

## 🔧 Configuration

Update database credentials in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce_db
    username: your_username
    password: your_password

app:
  order-fulfillment:
    thread-pool-size: 5          # Worker threads
    processing-delay-seconds: 10  # Fulfillment delay to simulate real-world order fulfillment
```

## 📝 Example API Usage

```bash
# 1. Get products
curl http://localhost:8080/api/products

# 2. Add to cart
curl -X POST http://localhost:8080/api/cart/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}'

# 3. Checkout
curl -X POST http://localhost:8080/api/orders/checkout

# 4. View orders
curl http://localhost:8080/api/orders
```

## 🛠️ Technology Stack

### Backend
- **Spring Boot 2.7.18** - Application framework
- **JPA/Hibernate** - ORM with PostgreSQL
- **PostgreSQL** - Database
- **Maven** - Build tool
- **Java 8** - Runtime

### Frontend
- **React 18** - UI framework
- **TypeScript** - Type-safe JavaScript
- **Vite** - Build tool and dev server
- **React Router** - Client-side routing
- **Axios** - HTTP client

## 📊 Sample Data

The application automatically initializes with 10 sample products on first run.

## 🐛 Troubleshooting

**Database Connection Issues:**
- Verify PostgreSQL is running: `brew services list | grep postgresql`
- Check database exists: `psql postgres -c "\l" | grep ecommerce_db`


## 📊 Optional: pgAdmin Database Management

pgAdmin 4 provides a graphical interface to manage and explore the PostgreSQL database.

### Installation
```bash
brew install --cask pgadmin4
```

### Connect to Database

1. **Launch pgAdmin 4** and set a master password (first time only)

2. **Create Server Connection:**
   - Right-click "Servers" → "Register" → "Server..."
   - **General Tab**: Name = `E-Commerce Local`
   - **Connection Tab**:
     - Host: `localhost`
     - Port: `5432`
     - Database: `ecommerce_db`
     - Username: `jason-ariel` (or your PostgreSQL username)
     - Password: *(your password, if set)*
   - Click "Save"

3. **Explore Database:**
   - Navigate to: `Servers → E-Commerce Local → Databases → ecommerce_db → Schemas → public → Tables`
   - Right-click any table → "View/Edit Data" → "All Rows"

### Useful Queries

**View Products:**
```sql
SELECT id, name, price, stock_quantity FROM products ORDER BY name;
```

**View Recent Orders:**
```sql
SELECT order_number, user_id, total_amount, status, created_at 
FROM orders 
ORDER BY created_at DESC 
LIMIT 10;
```

**Check Inventory:**
```sql
SELECT name, stock_quantity, 
       CASE WHEN stock_quantity = 0 THEN 'Out of Stock'
            WHEN stock_quantity < 10 THEN 'Low Stock'
            ELSE 'In Stock' END as status
FROM products;
```
