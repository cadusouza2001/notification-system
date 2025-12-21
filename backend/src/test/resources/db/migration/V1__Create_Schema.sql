-- Users
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(32),
    CONSTRAINT uq_users_email UNIQUE (email)
);

-- Categories
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uq_categories_name UNIQUE (name)
);

-- Channels
CREATE TABLE channels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uq_channels_name UNIQUE (name)
);

-- Subscriptions: Many-to-Many between Users and Categories
CREATE TABLE subscriptions (
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    subscribed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, category_id),
    CONSTRAINT fk_subscriptions_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_subscriptions_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);

-- User_Channels: Many-to-Many between Users and Channels
CREATE TABLE user_channels (
    user_id BIGINT NOT NULL,
    channel_id BIGINT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    selected_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, channel_id),
    CONSTRAINT fk_user_channels_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_channels_channel
        FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
);

-- Notification Logs: reference user, category, and channel
CREATE TABLE notification_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    channel_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_logs_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_logs_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT fk_logs_channel
        FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_subscriptions_user ON subscriptions (user_id);
CREATE INDEX idx_subscriptions_category ON subscriptions (category_id);
CREATE INDEX idx_user_channels_user ON user_channels (user_id);
CREATE INDEX idx_user_channels_channel ON user_channels (channel_id);
CREATE INDEX idx_notification_logs_user ON notification_logs (user_id);
CREATE INDEX idx_notification_logs_category ON notification_logs (category_id);
CREATE INDEX idx_notification_logs_channel ON notification_logs (channel_id);
CREATE INDEX idx_notification_logs_created_at ON notification_logs (created_at);
