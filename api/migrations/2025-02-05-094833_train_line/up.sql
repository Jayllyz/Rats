-- Your SQL goes here
CREATE TABLE train_lines (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    status TEXT NOT NULL
);

ALTER TABLE reports ADD COLUMN id_train_line INTEGER REFERENCES train_lines(id) ON DELETE SET NULL;
ALTER TABLE reports ALTER COLUMN id_user DROP NOT NULL;