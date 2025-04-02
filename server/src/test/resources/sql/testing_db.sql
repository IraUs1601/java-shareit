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
    requestor_id INTEGER NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT fk_item_requests_user FOREIGN KEY (requestor_id)
        REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица вещей
CREATE TABLE IF NOT EXISTS items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id INTEGER NOT NULL,
    request_id INTEGER,
    CONSTRAINT fk_items_owner FOREIGN KEY (owner_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_items_request FOREIGN KEY (request_id)
        REFERENCES item_requests(id) ON DELETE SET NULL
);

-- Таблица бронирований
CREATE TABLE IF NOT EXISTS bookings (
    id SERIAL PRIMARY KEY,
    item_id INTEGER NOT NULL,
    booker_id INTEGER NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT fk_bookings_item FOREIGN KEY (item_id)
        REFERENCES items(id),
    CONSTRAINT fk_bookings_booker FOREIGN KEY (booker_id)
        REFERENCES users(id)
);

-- Таблица комментариев
CREATE TABLE IF NOT EXISTS comments (
    id SERIAL PRIMARY KEY,
    item_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    text TEXT NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT fk_comments_item FOREIGN KEY (item_id)
        REFERENCES items(id),
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id)
        REFERENCES users(id)
);

INSERT INTO users (id, name, email) VALUES
    (1, 'Тестовый Пользователь', 'test@example.com'),
    (2, 'Тестовый Пользователь 2', 'test2@example.com'),
    (5, 'Иван Иванов', 'ivan@example.com'),
    (8, 'Петр Петров', 'petr@example.com');

INSERT INTO items (id, name, description, available, owner_id) VALUES
    (10, 'NAME', 'Description 1', true, 5),
    (15, 'NAME2', 'Description 2', true, 8);

INSERT INTO item_requests (id, description, requestor_id, created) VALUES
    (1, 'Нужна дрель для ремонта', 1, '2023-10-01 12:00:00'),
    (2, 'Ищу шуруповерт на выходные', 1, '2023-10-02 14:30:00'),
    (3, 'Требуется лестница для покраски потолка', 2, '2023-10-03 09:15:00');

INSERT INTO bookings (start_date, end_date, item_id, booker_id, status, created)
VALUES
    ('2023-07-20 12:00:00', '2023-07-25 12:00:00', 10, 5, 'APPROVED', '2023-07-19 00:00:00'),
    ('2023-08-01 09:30:00', '2023-08-05 18:00:00', 15, 8, 'WAITING', '2023-07-30 00:00:00');