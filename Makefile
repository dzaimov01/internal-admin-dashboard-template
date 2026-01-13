.PHONY: dev db backend frontend
.PHONY: seed

db:
	docker-compose up -d db

backend:
	cd backend && mvn spring-boot:run

frontend:
	cd frontend && npm install && npm run dev

dev: db backend frontend

seed:
	psql -h localhost -p 55432 -U admin -d admin_dashboard -f backend/scripts/seed.sql
