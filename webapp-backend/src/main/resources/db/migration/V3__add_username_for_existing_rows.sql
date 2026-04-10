ALTER TABLE user_accounts
ADD COLUMN IF NOT EXISTS username VARCHAR(100);

UPDATE user_accounts
SET username = CONCAT('user_', id)
WHERE username IS NULL OR username = '';

ALTER TABLE user_accounts
ALTER COLUMN username SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_user_accounts_username
ON user_accounts(username);
