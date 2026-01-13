insert into permissions (name, description) values
  ('customers:read', 'Read customers'),
  ('customers:write', 'Create/update/delete customers'),
  ('orders:read', 'Read orders'),
  ('orders:write', 'Create/update/delete orders'),
  ('tickets:read', 'Read tickets'),
  ('tickets:write', 'Create/update/delete tickets'),
  ('audit:read', 'Read audit logs'),
  ('metrics:read', 'Read metrics dashboard'),
  ('settings:read', 'Read settings'),
  ('settings:write', 'Update settings'),
  ('users:read', 'Read users'),
  ('users:write', 'Manage users'),
  ('system:read', 'Read system health');

insert into roles (name, description) values
  ('Admin', 'Full access to all modules'),
  ('Manager', 'Operational access to core modules'),
  ('Viewer', 'Read-only access');

insert into role_permissions (role_id, permission_id)
  select r.id, p.id from roles r join permissions p on r.name = 'Admin';

insert into role_permissions (role_id, permission_id)
  select r.id, p.id from roles r join permissions p on r.name = 'Manager'
  where p.name in (
    'customers:read','customers:write','orders:read','orders:write','tickets:read','tickets:write',
    'metrics:read','audit:read','users:read','system:read','settings:read'
  );

insert into role_permissions (role_id, permission_id)
  select r.id, p.id from roles r join permissions p on r.name = 'Viewer'
  where p.name in (
    'customers:read','orders:read','tickets:read','metrics:read','audit:read'
  );

insert into users (email, full_name, password_hash, status)
  values ('admin@acme.test', 'Admin User', '$2b$12$Rm/HB17SjWMCI5Jj/Ef75uZ0nnjBzdZ.ZjoMVCLek8AOYjiyfjaC6', 'ACTIVE');

insert into user_roles (user_id, role_id)
  select u.id, r.id from users u join roles r on u.email = 'admin@acme.test' and r.name = 'Admin';

insert into organization_settings (organization_name, timezone, support_email)
  values ('Acme Operations', 'UTC', 'support@acme.test');

insert into feature_flags (key, enabled, description) values
  ('beta-dashboard', false, 'Enable experimental dashboard widgets'),
  ('bulk-import', true, 'Allow CSV bulk import for customers');
