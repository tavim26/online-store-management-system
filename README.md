# Online Store Management System

A desktop application for running a small online store, built with **Java 21**, **JavaFX** and **MySQL**.
Customers can browse products, manage a cart and a wishlist, place orders and write reviews.
Administrators manage the product catalog and process orders.

The project started as a university assignment and was later refactored into a clean, layered application.

## Features

**Customers**
- Register and sign in (passwords are stored hashed)
- Browse and search the product catalog
- View product details, ratings and reviews
- Add products to a cart or a wishlist
- Place orders paid from the store balance
- Cancel orders that have not been shipped yet (stock and money are restored)
- Save payment cards and use them to top up the balance
- Review purchased products (one review per product)
- Edit profile data and change password

**Administrators**
- Create, edit and delete products
- View all orders and move them through `PAID → SHIPPED → DELIVERED`

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| User interface | JavaFX 21 (FXML + CSS) |
| Database | MySQL 8, accessed with plain JDBC |
| Build | Maven (wrapper included) |

## Architecture

The application is split into layers. Each layer only talks to the one directly below it.

```
UI (JavaFX controllers + FXML)
        │
        ▼
Service (business rules, validation, transactions)
        │
        ▼
DAO (SQL queries)
        │
        ▼
MySQL
```

| Package | Responsibility |
|---|---|
| `model` | Plain data classes: `User`, `Product`, `Order`, `OrderItem`, `Review`, `PaymentCard` |
| `dao` | One class per table group; only SQL and row mapping |
| `service` | Validation, business rules and transaction boundaries |
| `ui` | Screens, navigation, session and cart state |
| `config` | Database connection and transaction helper |

### Design decisions

- **Transactions for multi-step operations.** Placing an order inserts the order and its items, decreases stock and charges the balance in a single transaction. If any step fails, nothing is saved.
- **Atomic updates for stock and balance.** Queries like `UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?` check and update in one step, so two buyers can never take the same last item.
- **Prices come from the database.** The cart sends only product IDs and quantities; the order total is always calculated by the service.
- **Secure password storage.** Passwords are hashed with PBKDF2-HMAC-SHA256 (600,000 iterations, random salt), using only the JDK.
- **No sensitive card data.** Card numbers are validated with the Luhn algorithm, but only the last four digits are stored. CVV codes are never stored.
- **Responsive interface.** Database calls run on background threads, so the window never freezes.

## Getting Started

### Prerequisites

- JDK 21 or newer
- MySQL 8

### 1. Clone the repository

```bash
git clone https://github.com/tavim26/Online-Store-Management-System.git
cd Online-Store-Management-System
```

### 2. Create the database

In a MySQL console (for example `sudo mysql` on Linux), create the database and a dedicated user:

```sql
CREATE DATABASE store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'store_app'@'localhost' IDENTIFIED BY 'choose_a_password';
GRANT ALL PRIVILEGES ON store.* TO 'store_app'@'localhost';
```

Then load the schema and the sample products:

```bash
mysql -u store_app -p store < src/main/resources/db/schema.sql
mysql -u store_app -p store < src/main/resources/db/sample-data.sql
```

### 3. Configure the connection

Copy the example configuration and fill in your password:

```bash
cp db.properties.example db.properties
```

```properties
db.url=jdbc:mysql://localhost:3306/store
db.user=store_app
db.password=choose_a_password
```

`db.properties` is ignored by Git, so your credentials stay local.

### 4. Run the application

```bash
./mvnw javafx:run
```

On Windows use `mvnw.cmd javafx:run`. Alternatively, open the project in IntelliJ IDEA and run the `App` class.

### 5. Create an administrator

Register a normal account in the app, then promote it:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your@email.com';
```

Sign in again and the **Admin** menu appears in the top bar.

### Test data

| What | Value |
|---|---|
| Valid CNP | `1960101410019`, `2990515400027`, `1850712123451` |
| Test card number | `4242 4242 4242 4242` (any future expiry date) |

Each CNP can be used for one account only.

## Database

| Table | Content |
|---|---|
| `users` | Accounts, roles and store balance |
| `products` | Catalog with price and stock |
| `orders` / `order_items` | Orders and the products in each order, with the price paid |
| `reviews` | Ratings from 1 to 5, one per user and product |
| `wishlist_items` | Products saved by each user |
| `payment_cards` | Card type, last four digits and expiry date |

Constraints (`UNIQUE`, `CHECK`, foreign keys) enforce the main rules directly in the database, as a second line of defense after the service layer.

## Project Structure

```
src/main/
├── java/io/github/tavim26/onlinestore/
│   ├── App.java
│   ├── config/       Database connection and transactions
│   ├── model/        Domain classes
│   ├── dao/          Data access (JDBC)
│   ├── service/      Business logic and validation
│   └── ui/           Navigation, session, cart, helpers
│       └── controller/
└── resources/
    ├── db/           schema.sql, sample-data.sql
    └── io/github/tavim26/onlinestore/
        ├── view/     FXML screens
        └── css/      Stylesheet
```

## Limitations

- Payments are simulated: cards only top up the store balance.
- Administrator access is enforced in the user interface, which is sufficient for a single-user desktop application but not for a multi-client server.
- The project has no automated tests.

## License

This project is licensed under the [MIT License](LICENSE).
