-- Your SQL goes here
CREATE TABLE users_lines (
    id_user INTEGER NOT NULL,
    id_line INTEGER NOT NULL,
    PRIMARY KEY (id_user, id_line),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_line) REFERENCES train_lines(id) ON DELETE CASCADE
)