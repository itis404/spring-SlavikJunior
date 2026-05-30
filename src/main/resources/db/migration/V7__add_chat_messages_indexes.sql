CREATE INDEX IF NOT EXISTS idx_chat_messages_order_id ON chat_messages (order_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_sent_at ON chat_messages (sent_at);
