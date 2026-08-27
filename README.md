# linkedin-scrapper

A multi-module LinkedIn profile scraper project with:
- **Backend**: Spring Boot API (`scrapper-backend`) that scrapes and returns normalized LinkedIn profile data.
- **Frontend**: Flutter Web app (`scrapper-frontend`) that accepts a LinkedIn URL and displays profile details.

## Project structure

- `scrapper-backend` - Java/Spring Boot backend service
- `scrapper-frontend` - Flutter web frontend

## Prerequisites

- Java 21
- Flutter SDK (with web support enabled)
- Chrome (or another Flutter-supported web browser)
- Docker Desktop (for containerized run)

## Backend setup and run

1. Set the ZenRows API key:
   - PowerShell: `$env:ZENROWS_API_KEY="your_api_key"`
2. Start the backend from repository root:
   - `.\scrapper-backend\mvnw.cmd -f .\scrapper-backend\pom.xml spring-boot:run`
3. Backend will run on:
   - `http://localhost:8080`
4. Swagger UI:
   - `http://localhost:8080/swagger-ui.html`
5. LinkedIn scrape endpoints:
   - `POST /api/linkedin` with optional query param `type`
   - `type=hybrid` or omitted: hybrid parser (default)
   - `type=html`: HTML parser response
   - `type=parsed`: parsed JSON response

## Frontend setup and run

1. Move to frontend module:
   - `cd .\scrapper-frontend`
2. Install dependencies:
   - `flutter pub get`
3. Run on web:
   - `flutter run -d chrome`

The frontend calls the backend using same-origin path `/api/linkedin` by default (recommended for EC2 + reverse proxy).
You can override the API base URL at build/run time with:
- `--dart-define=API_BASE_URL=http://localhost:8080` (or your backend URL)

## Dockerized run (frontend + backend)

1. Set the ZenRows API key in your shell (or in a `.env` file):
   - PowerShell: `$env:ZENROWS_API_KEY="your_api_key"`
2. From repository root, build and start both services:
   - `docker compose up --build`
3. Access:
   - Frontend: `http://localhost`
   - Backend API: `http://localhost:8080`
   - Swagger UI (via frontend proxy): `http://localhost/swagger-ui/index.html`

In Docker, the frontend is served by Nginx and proxies `/api`, `/swagger-ui`, and `/api-docs` to the backend service. This keeps frontend API calls same-origin and avoids browser CORS issues.