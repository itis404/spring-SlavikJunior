-- Users
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    phone       VARCHAR(20)  NOT NULL UNIQUE,
    email       VARCHAR(255),
    role        VARCHAR(20)  NOT NULL DEFAULT 'CLIENT',
    bonus_points INT          NOT NULL DEFAULT 0,
    password_hash VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP
);

-- Refresh tokens (JWT)
CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users (id),
    token       VARCHAR(512) NOT NULL UNIQUE,
    expires_at  TIMESTAMP    NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP
);

-- OTP codes for phone verification
CREATE TABLE sms_codes (
    id          BIGSERIAL PRIMARY KEY,
    phone       VARCHAR(20) NOT NULL,
    code        VARCHAR(6)  NOT NULL,
    expires_at  TIMESTAMP   NOT NULL,
    used        BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP
);

-- Menu items
CREATE TABLE menu_items (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    category     VARCHAR(50)  NOT NULL,
    description  TEXT,
    photo_url    VARCHAR(512),
    is_available BOOLEAN      NOT NULL DEFAULT TRUE,
    is_hidden    BOOLEAN      NOT NULL DEFAULT FALSE,
    is_seasonal  BOOLEAN      NOT NULL DEFAULT FALSE,
    valid_from   TIMESTAMP,
    valid_to     TIMESTAMP,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP
);

-- Volumes per menu item (250 ml, 350 ml, 450 ml, etc.)
CREATE TABLE menu_item_volumes (
    id           BIGSERIAL PRIMARY KEY,
    menu_item_id BIGINT         NOT NULL REFERENCES menu_items (id),
    volume_ml    INT            NOT NULL,
    price        DECIMAL(10, 2) NOT NULL,
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP
);

-- Modifiers (syrups, alternative milk, vitamin shots, marshmallow)
CREATE TABLE modifiers (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255)   NOT NULL,
    category     VARCHAR(50)    NOT NULL,
    price        DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP
);

-- M2M: which modifiers are compatible with which menu items
CREATE TABLE menu_item_modifiers (
    menu_item_id BIGINT NOT NULL REFERENCES menu_items (id),
    modifier_id  BIGINT NOT NULL REFERENCES modifiers (id),
    PRIMARY KEY (menu_item_id, modifier_id)
);

-- Orders
CREATE TABLE orders (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT         NOT NULL REFERENCES users (id),
    order_status     VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    payment_status   VARCHAR(20)    NOT NULL DEFAULT 'UNPAID',
    total_price      DECIMAL(10, 2) NOT NULL,
    tbank_payment_id VARCHAR(255),
    comment          TEXT,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMP
);

-- Line items within an order
CREATE TABLE order_items (
    id             BIGSERIAL PRIMARY KEY,
    order_id       BIGINT         NOT NULL REFERENCES orders (id),
    menu_item_id   BIGINT         NOT NULL REFERENCES menu_items (id),
    volume_id      BIGINT         NOT NULL REFERENCES menu_item_volumes (id),
    quantity       INT            NOT NULL,
    price_snapshot DECIMAL(10, 2) NOT NULL,
    comment        TEXT,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at     TIMESTAMP
);

-- Modifiers chosen for a specific order item (M2M bridge with price snapshot)
CREATE TABLE order_item_modifiers (
    id             BIGSERIAL PRIMARY KEY,
    order_item_id  BIGINT         NOT NULL REFERENCES order_items (id),
    modifier_id    BIGINT         NOT NULL REFERENCES modifiers (id),
    price_snapshot DECIMAL(10, 2) NOT NULL,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at     TIMESTAMP
);

-- Customer feedback
CREATE TABLE feedback (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL REFERENCES users (id),
    text       TEXT      NOT NULL,
    rating     INT       NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- Indexes
CREATE INDEX idx_users_phone ON users (phone);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens (token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_sms_codes_phone ON sms_codes (phone);
CREATE INDEX idx_menu_items_category ON menu_items (category);
CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_order_status ON orders (order_status);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
