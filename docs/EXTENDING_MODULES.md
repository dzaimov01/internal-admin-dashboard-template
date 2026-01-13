# Extending Modules

## Add a New Module in 10 Minutes
1. **Create a domain entity** in `backend/src/main/java/com/example/admin/domain`.
2. **Add repository** under `infra`.
3. **Create service** under `app`.
4. **Create controller** under `api` with RBAC permissions.
5. **Add Flyway migration** for the new table.
6. **Seed permissions** in `V2__seed.sql`.
7. **Create frontend page** under `frontend/src/app/(dashboard)/<module>`.
8. **Add React Query hooks** in `frontend/src/lib/queries.ts`.
9. **Update navigation** in `frontend/src/components/Sidebar.tsx`.

## Recommended Patterns
- Use `@PreAuthorize` for API protection.
- Emit audit events on every write.
- Keep DTOs small and explicit.
