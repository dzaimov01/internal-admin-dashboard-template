# Deployment

## Local Development
1. `docker-compose up -d db` (Postgres exposed on `localhost:55432`)
2. `cd backend && mvn spring-boot:run`
3. `cd frontend && npm install && npm run dev`

## Demo Seed Data
Load sample data with:
```bash
psql -h localhost -p 55432 -U admin -d admin_dashboard -f backend/scripts/seed.sql
```

## Docker Compose
Use `docker-compose.yml` to run PostgreSQL locally. The backend and frontend can be containerized later.

## Environment Variables
Backend (`backend/.env.example`):
- `DB_URL`, `DB_USER`, `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
 - `app.build-version`

Frontend (`frontend/.env.example`):
- `NEXT_PUBLIC_API_URL`

## Reverse Proxy
Place a reverse proxy (NGINX/Caddy) in front of the UI and API.
- Route `/api` to the backend.
- Serve the Next.js app from root.
