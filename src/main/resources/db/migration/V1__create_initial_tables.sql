create SCHEMA IF NOT EXISTS app;

create TABLE IF NOT EXISTS app.user_profiles (
    user_id UUID PRIMARY KEY,
    loyalty_level TEXT NOT NULL,
    external_balance NUMERIC(10, 2) NOT NULL
);

create TABLE IF NOT EXISTS app.inbox_events (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL
);