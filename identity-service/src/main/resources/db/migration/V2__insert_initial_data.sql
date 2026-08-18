-- Tạo sẵn các Role mặc định
INSERT INTO roles (name) VALUES ('CUSTOMER');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('TELLER');

-- Tạo sẵn các Permission cơ bản
INSERT INTO permissions (name) VALUES ('VIEW_BALANCE');
INSERT INTO permissions (name) VALUES ('TRANSFER_MONEY');
INSERT INTO permissions (name) VALUES ('MANAGE_USERS');

-- Gán quyền cho ADMIN
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ADMIN' AND p.name IN ('VIEW_BALANCE', 'TRANSFER_MONEY', 'MANAGE_USERS');

-- Gán quyền cho CUSTOMER
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'CUSTOMER' AND p.name IN ('VIEW_BALANCE', 'TRANSFER_MONEY');
