# Security Notes

## Implemented
- JWT-based auth with role + permission claims
- Refresh token stored server-side (`refresh_tokens` table)
- Password hashing with BCrypt
- Input validation via Jakarta Validation and Zod
- Rate limiting on login endpoint
- CORS allowlist
- Audit logging for write events

## Threats & Mitigations
- **Credential stuffing**: rate limit + strong password policy
- **CSRF**: API uses Authorization header tokens (no cookies)
- **Data exposure**: no secrets in logs, explicit DTOs
- **Broken access control**: method-level RBAC on every endpoint

## JWT Strategy
- Access tokens are short-lived (15 minutes by default) and sent via `Authorization: Bearer`.
- Refresh tokens are long-lived and stored in the database.
- Clients exchange refresh tokens for new access tokens at `/api/auth/refresh`.
- Token rotation can be added by invalidating old refresh tokens on refresh.

## Enterprise Features (Docs Only)
- SSO/SAML/OIDC integration
- Multi-tenant isolation
- Row-level security
- Compliance evidence packs
