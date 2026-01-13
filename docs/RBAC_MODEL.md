# RBAC Model

## Roles
- Admin: full access
- Manager: core operational access (customers, orders, tickets, metrics, audit)
- Viewer: read-only access

## Permissions
Permissions are fine-grained strings applied to endpoints and UI gates.

Examples:
- `customers:read`, `customers:write`
- `orders:read`, `orders:write`
- `tickets:read`, `tickets:write`
- `audit:read`, `metrics:read`
- `users:read`, `users:write`
- `settings:read`, `settings:write`
- `system:read`

## Enforcement
- Backend: `@PreAuthorize` checks on controllers and method-level security.
- Frontend: Admin-only sections are gated in navigation + page-level gates.

## Customizing Roles
1. Add a new role record in Flyway seed or admin UI.
2. Assign permissions via `role_permissions`.
3. Update frontend nav gates if necessary.
