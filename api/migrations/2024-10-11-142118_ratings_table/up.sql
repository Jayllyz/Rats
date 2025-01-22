CREATE TABLE ratings (
    id SERIAL PRIMARY KEY,
    id_receiver INTEGER NOT NULL,
    id_sender INTEGER NOT NULL,
    stars INT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_receiver) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_sender) REFERENCES users(id) ON DELETE CASCADE
);
