-- Add receipt photo URL to orders
ALTER TABLE orders
    ADD COLUMN receipt_photo_url VARCHAR(512);

-- Chat messages between barista and client (tied to a specific order)
CREATE TABLE chat_messages (
    id       BIGSERIAL PRIMARY KEY,
    order_id BIGINT    NOT NULL REFERENCES orders (id),
    sender_id BIGINT   NOT NULL REFERENCES users (id),
    text     TEXT      NOT NULL,
    sent_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_chat_messages_order_id ON chat_messages (order_id);
CREATE INDEX idx_chat_messages_sent_at  ON chat_messages (order_id, sent_at);
