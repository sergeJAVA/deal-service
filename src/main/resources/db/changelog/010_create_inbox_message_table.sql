CREATE TABLE IF NOT EXISTS inbox_messages(
    message_id UUID PRIMARY KEY,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    received_at TIMESTAMP NOT NULL,
    processing_attempts INT NOT NULL DEFAULT 0
);