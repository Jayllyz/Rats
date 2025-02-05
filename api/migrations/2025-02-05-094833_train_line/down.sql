-- This file should undo anything in `up.sql`
ALTER TABLE reports DROP COLUMN id_train_line;
DROP TABLE train_lines;
