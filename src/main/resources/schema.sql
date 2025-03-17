-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);

-- Таблица запросов вещей
CREATE TABLE IF NOT EXISTS item_requests (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    requestor_id INT NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT fk_item_requests_user FOREIGN KEY (requestor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица вещей
CREATE TABLE IF NOT EXISTS items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id INT NOT NULL,
    request_id INT,
    CONSTRAINT fk_items_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_items_request FOREIGN KEY (request_id) REFERENCES item_requests(id) ON DELETE SET NULL
);

-- Таблица бронирований
CREATE TABLE IF NOT EXISTS bookings (
    id SERIAL PRIMARY KEY,
    item_id INT NOT NULL,
    booker_id INT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT fk_bookings_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_bookings_booker FOREIGN KEY (booker_id) REFERENCES users(id)
);

-- Таблица комментариев
CREATE TABLE IF NOT EXISTS comments (
    id SERIAL PRIMARY KEY,
    item_id INT NOT NULL,
    author_id INT NOT NULL,
    text TEXT NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT fk_comments_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id)
);