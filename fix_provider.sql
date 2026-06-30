-- Fix invalid AuthProvider values in the user table
-- Convert all uppercase and invalid values to lowercase 'local'

UPDATE user SET provider = 'local' WHERE provider IS NULL;
UPDATE user SET provider = 'local' WHERE provider = 'LOCAL';
UPDATE user SET provider = 'local' WHERE provider = 'FACEBOOK';
UPDATE user SET provider = 'local' WHERE provider = 'GOOGLE';
UPDATE user SET provider = 'local' WHERE provider = 'GITHUB';

-- Ensure all provider values are lowercase
UPDATE user SET provider = LOWER(provider) WHERE provider IS NOT NULL;
