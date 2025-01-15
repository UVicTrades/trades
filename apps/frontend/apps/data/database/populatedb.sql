CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(32) NOT NULL, -- might have to change length later
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) NOT NULL,
    cents INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stocks (
    stock_id SERIAL PRIMARY KEY,
    stock_symbol VARCHAR(4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    stock_symbol INT REFERENCES stocks(stock_id) NOT NULL,
    user_id INT REFERENCES users(user_id) NOT NULL,
    quantity INT NOT NULL CHECK (quantity <> 0), -- postitive is buy, negative is sell
    cents INT CHECK (cents >= 0), -- null if market
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trades (
    trade_id SERIAL PRIMARY KEY,
    stock_symbol INT REFERENCES stocks(stock_id) NOT NULL,
    seller_id INT REFERENCES users(user_id) NOT NULL,
    buyer_id INT REFERENCES users(user_id) NOT NULL,
    seller_order INT REFERENCES orders(order_id) NOT NULL,
    buyer_order INT REFERENCES orders(order_id) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    cents INT NOT NULL CHECK (cents >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
