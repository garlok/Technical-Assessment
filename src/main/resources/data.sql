-- Insert into users
INSERT INTO users (id, user_name) VALUES ('1d4e2e4b-8c1e-4c9b-b5ad-1a229d66d1d8', 'tester');

-- Insert into wallet
INSERT INTO wallet (id, user_name, balance, balance_currency, updated_time) VALUES
('2f4e6b7a-6e2f-4c89-ae2c-3d6f1a26e2d2', 'tester', 50000.00, 'USDT', CURRENT_TIMESTAMP);
