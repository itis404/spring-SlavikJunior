CREATE TABLE shop_settings (
    id                  BIGINT  PRIMARY KEY,
    is_accepting_orders BOOLEAN NOT NULL DEFAULT TRUE,
    closed_message      VARCHAR(255)
);

INSERT INTO shop_settings (id, is_accepting_orders, closed_message) VALUES (1, TRUE, NULL);
