insert into customers (name, email, status, notes)
values
  ('Northwind Traders', 'northwind@example.com', 'ACTIVE', 'High volume account'),
  ('Blue Harbor', 'blueharbor@example.com', 'PAUSED', 'Renewal pending'),
  ('Summit Retail', 'summit@example.com', 'ACTIVE', 'Onboarded Q1');

insert into orders (customer_id, amount, status, created_at)
values
  ((select id from customers where email = 'northwind@example.com'), 1200.00, 'NEW', now()),
  ((select id from customers where email = 'blueharbor@example.com'), 450.50, 'PROCESSING', now() - interval '3 days'),
  ((select id from customers where email = 'summit@example.com'), 780.20, 'FULFILLED', now() - interval '10 days');

insert into tickets (title, priority, status, assigned_to)
values
  ('Investigate payment retries', 'HIGH', 'OPEN', (select id from users where email = 'admin@acme.test')),
  ('Update contract docs', 'MEDIUM', 'IN_PROGRESS', (select id from users where email = 'admin@acme.test')),
  ('Export history report', 'LOW', 'RESOLVED', (select id from users where email = 'admin@acme.test'));
