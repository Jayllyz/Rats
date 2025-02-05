-- This file should undo anything in `up.sql`
DELETE FROM train_lines
WHERE name IN ('Ligne 1', 'Ligne 2', 'Ligne 3', 'Ligne 3bis', 'Ligne 4', 
                'Ligne 5', 'Ligne 6', 'Ligne 7', 'Ligne 7bis', 'Ligne 8', 
                'Ligne 9', 'Ligne 10', 'Ligne 11', 'Ligne 12', 'Ligne 13', 'Ligne 14');