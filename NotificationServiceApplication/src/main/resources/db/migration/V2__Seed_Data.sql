-- Seed static categories
INSERT INTO categories (name) VALUES ('Sports'), ('Finance'), ('Movies');

-- Seed static channels
INSERT INTO channels (name) VALUES ('SMS'), ('E-Mail'), ('Push Notification');

-- Seed mock users
INSERT INTO users (name, email, phone_number) VALUES
('Alice Johnson', 'alice@example.com', '+155555501'),
('Bob Smith', 'bob@example.com', '+155555502'),
('Carol Davis', 'carol@example.com', '+155555503');

-- Subscriptions:
-- Alice -> Sports
INSERT INTO subscriptions (user_id, category_id)
SELECT u.id, c.id FROM users u
JOIN categories c ON c.name = 'Sports'
WHERE u.email = 'alice@example.com';

-- Bob -> Finance
INSERT INTO subscriptions (user_id, category_id)
SELECT u.id, c.id FROM users u
JOIN categories c ON c.name = 'Finance'
WHERE u.email = 'bob@example.com';

-- Carol -> Movies and Sports
INSERT INTO subscriptions (user_id, category_id)
SELECT u.id, c.id FROM users u
JOIN categories c ON c.name IN ('Movies', 'Sports')
WHERE u.email = 'carol@example.com';

-- User Channels:
-- Alice -> SMS
INSERT INTO user_channels (user_id, channel_id, enabled)
SELECT u.id, ch.id, TRUE FROM users u
JOIN channels ch ON ch.name = 'SMS'
WHERE u.email = 'alice@example.com';

-- Bob -> E-Mail
INSERT INTO user_channels (user_id, channel_id, enabled)
SELECT u.id, ch.id, TRUE FROM users u
JOIN channels ch ON ch.name = 'E-Mail'
WHERE u.email = 'bob@example.com';

-- Carol -> Push Notification and E-Mail
INSERT INTO user_channels (user_id, channel_id, enabled)
SELECT u.id, ch.id, TRUE FROM users u
JOIN channels ch ON ch.name IN ('Push Notification', 'E-Mail')
WHERE u.email = 'carol@example.com';
