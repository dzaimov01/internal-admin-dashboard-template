create table permissions (
  id bigserial primary key,
  name varchar(120) not null unique,
  description varchar(255) not null
);

create table roles (
  id bigserial primary key,
  name varchar(120) not null unique,
  description varchar(255) not null
);

create table role_permissions (
  role_id bigint not null references roles(id) on delete cascade,
  permission_id bigint not null references permissions(id) on delete cascade,
  primary key (role_id, permission_id)
);

create table users (
  id bigserial primary key,
  email varchar(255) not null unique,
  full_name varchar(255) not null,
  password_hash varchar(255) not null,
  status varchar(30) not null
);

create table user_roles (
  user_id bigint not null references users(id) on delete cascade,
  role_id bigint not null references roles(id) on delete cascade,
  primary key (user_id, role_id)
);

create table customers (
  id bigserial primary key,
  name varchar(255) not null,
  email varchar(255) not null unique,
  status varchar(30) not null,
  notes varchar(2000)
);

create table orders (
  id bigserial primary key,
  customer_id bigint not null references customers(id),
  amount numeric(12,2) not null,
  status varchar(30) not null,
  created_at timestamptz not null
);

create table tickets (
  id bigserial primary key,
  title varchar(255) not null,
  priority varchar(30) not null,
  status varchar(30) not null,
  assigned_to bigint references users(id)
);

create table audit_logs (
  id bigserial primary key,
  actor_email varchar(255) not null,
  action varchar(120) not null,
  entity_type varchar(120) not null,
  entity_id varchar(120) not null,
  change_summary varchar(4000),
  created_at timestamptz not null,
  ip_address varchar(80) not null,
  user_agent varchar(255) not null
);

create table refresh_tokens (
  id bigserial primary key,
  user_id bigint not null references users(id) on delete cascade,
  token varchar(512) not null unique,
  expires_at timestamptz not null
);

create table organization_settings (
  id bigserial primary key,
  organization_name varchar(255) not null,
  timezone varchar(120) not null,
  support_email varchar(255) not null
);

create table feature_flags (
  id bigserial primary key,
  key varchar(120) not null unique,
  enabled boolean not null,
  description varchar(255) not null
);
