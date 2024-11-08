-- This file should undo anything in `up.sql`
ALTER TABLE users DROP COLUMN latitude;
ALTER TABLE users DROP COLUMN longitude;