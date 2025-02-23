-- UP Migration
CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    id_sender SERIAL NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_sender
        FOREIGN KEY (id_sender)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Indexes for better query performance
CREATE INDEX idx_messages_sender 
    ON messages(id_sender);

CREATE INDEX idx_messages_created_at 
    ON messages(created_at DESC);
