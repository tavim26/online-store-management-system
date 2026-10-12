-- Creates all tables for the online store.
-- Safe to re-run: existing tables are dropped first.

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS wishlist_items, reviews, order_items, orders, payment_cards, products, users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
                       id            INT AUTO_INCREMENT PRIMARY KEY,
                       role          VARCHAR(20)   NOT NULL DEFAULT 'CUSTOMER',
                       first_name    VARCHAR(50)   NOT NULL,
                       last_name     VARCHAR(50)   NOT NULL,
                       email         VARCHAR(100)  NOT NULL UNIQUE,
                       password_hash VARCHAR(255)  NOT NULL,
                       cnp           CHAR(13)      NOT NULL UNIQUE,
                       address       VARCHAR(255)  NOT NULL,
                       phone         VARCHAR(15)   NOT NULL,
                       balance       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                       created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT chk_users_role    CHECK (role IN ('CUSTOMER', 'ADMIN')),
                       CONSTRAINT chk_users_balance CHECK (balance >= 0)
);

CREATE TABLE products (
                          id          INT AUTO_INCREMENT PRIMARY KEY,
                          name        VARCHAR(100)  NOT NULL,
                          description TEXT          NOT NULL,
                          price       DECIMAL(10,2) NOT NULL,
                          stock       INT           NOT NULL DEFAULT 0,
                          CONSTRAINT chk_products_price CHECK (price >= 0),
                          CONSTRAINT chk_products_stock CHECK (stock >= 0)
);

CREATE TABLE payment_cards (
                               id          INT AUTO_INCREMENT PRIMARY KEY,
                               user_id     INT         NOT NULL,
                               card_type   VARCHAR(20) NOT NULL,
                               last_four   CHAR(4)     NOT NULL,
                               expiry_date DATE        NOT NULL,
                               CONSTRAINT fk_cards_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE orders (
                        id               INT AUTO_INCREMENT PRIMARY KEY,
                        user_id          INT           NOT NULL,
                        order_date       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
                        shipping_address VARCHAR(255)  NOT NULL,
                        total            DECIMAL(10,2) NOT NULL,
                        CONSTRAINT fk_orders_user    FOREIGN KEY (user_id) REFERENCES users (id),
                        CONSTRAINT chk_orders_status CHECK (status IN ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
                        CONSTRAINT chk_orders_total  CHECK (total >= 0)
);

CREATE TABLE order_items (
                             order_id   INT           NOT NULL,
                             product_id INT           NOT NULL,
                             quantity   INT           NOT NULL,
                             unit_price DECIMAL(10,2) NOT NULL,
                             PRIMARY KEY (order_id, product_id),
                             CONSTRAINT fk_items_order     FOREIGN KEY (order_id)   REFERENCES orders (id) ON DELETE CASCADE,
                             CONSTRAINT fk_items_product   FOREIGN KEY (product_id) REFERENCES products (id),
                             CONSTRAINT chk_items_quantity CHECK (quantity > 0)
);

CREATE TABLE reviews (
                         id         INT AUTO_INCREMENT PRIMARY KEY,
                         user_id    INT      NOT NULL,
                         product_id INT      NOT NULL,
                         rating     TINYINT  NOT NULL,
                         comment    TEXT,
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT uq_reviews_user_product UNIQUE (user_id, product_id),
                         CONSTRAINT fk_reviews_user    FOREIGN KEY (user_id)    REFERENCES users (id)    ON DELETE CASCADE,
                         CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
                         CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE TABLE wishlist_items (
                                user_id    INT      NOT NULL,
                                product_id INT      NOT NULL,
                                added_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (user_id, product_id),
                                CONSTRAINT fk_wishlist_user    FOREIGN KEY (user_id)    REFERENCES users (id)    ON DELETE CASCADE,
                                CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);