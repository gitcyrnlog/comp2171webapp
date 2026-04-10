INSERT INTO user_accounts(username, full_name, email, password_hash, role, block_code, active)
SELECT 'admin', 'System Admin', 'admin@gah.local', '$2a$10$7Aypf6RXQhA7i7Dk9AJ3I.iXFY7P5VxIklP9bNbK8VYfA/OI44ViG', 'ADMIN', NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'admin');

INSERT INTO user_accounts(username, full_name, email, password_hash, role, block_code, active)
SELECT 'maint1', 'Maintenance Worker One', 'maint1@gah.local', '$2a$10$7Aypf6RXQhA7i7Dk9AJ3I.iXFY7P5VxIklP9bNbK8VYfA/OI44ViG', 'MAINTENANCE_WORKER', NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'maint1');

INSERT INTO user_accounts(username, full_name, email, password_hash, role, block_code, active)
SELECT 'guard1', 'Security Guard One', 'guard1@gah.local', '$2a$10$7Aypf6RXQhA7i7Dk9AJ3I.iXFY7P5VxIklP9bNbK8VYfA/OI44ViG', 'SECURITY_GUARD', NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'guard1');
