# Architecture

## Overview
This template is a monorepo with a Spring Boot API and a Next.js 14 UI. The backend enforces RBAC, audit logging, and validation; the frontend provides a modular admin shell with typed data access.

## Backend Structure
```
backend/src/main/java/com/example/admin
├─ api        # REST controllers + DTOs
├─ app        # application services (use cases)
├─ domain     # entities + enums
├─ infra      # repositories + persistence adapters
└─ config     # security, JWT, CORS
```

### Backend Request Flow
```
HTTP Request
  -> JwtAuthFilter (auth) -> Controller
  -> App Service -> Repository -> PostgreSQL
  -> AuditService (on write actions)
```

## Frontend Structure
```
frontend/src
├─ app                # Next.js app router
│  ├─ (dashboard)     # auth-gated pages
│  └─ login
├─ components         # layout + UI primitives
└─ lib                # api client, auth, queries
```

### Frontend Data Flow
```
React Query -> apiClient -> /api/* -> Spring Boot
```

## Deployment Diagram
```
[Browser]
   |
   | HTTPS
   v
[Next.js App] ---> [Spring Boot API] ---> [PostgreSQL]
```

## Extension Points
- Add new modules: new entity + repository + service + controller + UI route.
- RBAC: update permissions + role mappings (Flyway seed + UI checks).
- Audit: extend AuditService to include diffing or richer change metadata.
- Metrics: extend MetricsService with warehouse integrations.
