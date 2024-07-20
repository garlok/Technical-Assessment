CREATE TABLE users (
    id UUID PRIMARY KEY,
    user_name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE wallet (
    id UUID PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    balance DECIMAL(19, 6) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_name) REFERENCES users(user_name)
);
