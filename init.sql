CREATE TABLE api_call_log_entity (
    id SERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    parameters TEXT,
    response TEXT,
    error TEXT
);
