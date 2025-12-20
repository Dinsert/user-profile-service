CREATE SCHEMA IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS app.user_profiles (
    user_id UUID PRIMARY KEY,
    loyalty_level TEXT NOT NULL,
    external_balance NUMERIC(10, 2) NOT NULL
);