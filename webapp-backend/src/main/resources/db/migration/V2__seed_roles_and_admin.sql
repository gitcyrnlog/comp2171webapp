-- Seed representative users for diagram-aligned role model.
INSERT INTO user_accounts(username, full_name, email, password_hash, role, block_code, active)
VALUES
    ('admin', 'System Admin', 'admin@gah.local', '$2a$10$7Aypf6RXQhA7i7Dk9AJ3I.iXFY7P5VxIklP9bNbK8VYfA/OI44ViG', 'ADMIN', NULL, TRUE),
    ('maint1', 'Maintenance Worker One', 'maint1@gah.local', '$2a$10$7Aypf6RXQhA7i7Dk9AJ3I.iXFY7P5VxIklP9bNbK8VYfA/OI44ViG', 'MAINTENANCE_WORKER', NULL, TRUE),
    ('guard1', 'Security Guard One', 'guard1@gah.local', '$2a$10$7Aypf6RXQhA7i7Dk9AJ3I.iXFY7P5VxIklP9bNbK8VYfA/OI44ViG', 'SECURITY_GUARD', NULL, TRUE)
ON CONFLICT (email) DO NOTHING;

-- BCrypt hash above corresponds to: ChangeMe123!
