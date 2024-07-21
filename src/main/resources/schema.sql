CREATE TABLE users (
    id UUID PRIMARY KEY,
    user_name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE wallet (
    id UUID PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    balance DECIMAL(30,8) NOT NULL,
    balance_currency VARCHAR(255) NOT NULL,
    updated_time TIMESTAMP,
    FOREIGN KEY (user_name) REFERENCES users(user_name)
);