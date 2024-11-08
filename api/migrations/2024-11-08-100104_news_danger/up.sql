-- Your SQL goes here
CREATE TYPE report_type AS ENUM ('Individu dangereux', 'Pickpocket', 'Accident', 'Autre');

CREATE TABLE reports (
    id SERIAL PRIMARY KEY,
    id_user INTEGER REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    report_type report_type NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);