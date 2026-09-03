# LinkedIn Profile Scraper

A multi-module implementation of a LinkedIn profile scraping challenge. The backend API accepts a LinkedIn profile URL and returns structured JSON built from scraped data.

> **Important:** LinkedIn generally does not directly allow unauthorized scraping under its platform terms.  
> This project uses the **ZenRows API** to fetch profile page content instead of directly scraping LinkedIn.

This repository contains:

- **`scraper-backend`**: Spring Boot REST API (core application)
- **`scraper-frontend`**: Flutter Web client for visualizing scraped results (demo/visualization layer)
- **`docker-compose.yml`**: Runs backend and frontend together

## Features

- `POST /api/linkedin` scrape endpoint
- Parser modes via query param: `hybrid` (default), `html`, `parsed`
- Structured extraction for profile basics, experience, education, languages, posts/articles, and profile/cover images
- OpenAPI + Swagger UI (`/api-docs`, `/swagger-ui.html`)
- Global API error payloads
- Dockerized backend and frontend
- Nginx reverse proxy in frontend container for same-origin API/docs routing

> Current implementation does **not** expose dedicated structured fields for skills or certifications.

## API (Single Endpoint)

### Domains

| Environment | URL |
|---|---|
| **Production** | `https://curiodesk.xyz` |
| **Local Development** | `http://localhost:8080` |

### Endpoint Details

- **Method**: `POST`
- **Endpoint**: `/api/linkedin`
- **Query param (optional)**: `mode=hybrid|html|parsed` (default: `hybrid`)
- **Payload (`application/json`)**:

```json
{
  "url": "https://www.linkedin.com/in/satyanadella/"
}
```

### Sample POST request

**Production**:
```bash
curl -X POST "https://curiodesk.xyz/api/linkedin?mode=hybrid" \
  -H "Content-Type: application/json" \
  -d "{\"url\":\"https://www.linkedin.com/in/satyanadella/\"}"
```

**Local**:
```bash
curl -X POST "http://localhost:8080/api/linkedin?mode=hybrid" \
  -H "Content-Type: application/json" \
  -d "{\"url\":\"https://www.linkedin.com/in/satyanadella/\"}"
```

## Prerequisites

- **Java 21** (backend build/runtime)
- **Maven Wrapper** in `scraper-backend` (`mvnw`, `mvnw.cmd`)
- **Flutter SDK** with web support (frontend local run)
- **A Flutter-supported browser** (e.g., Chrome)
- **Docker + Docker Compose** (containerized run)

## Configuration & Environment Variables

### Required

| Variable | Used by | Required | Purpose | Example |
|---|---|---|---|---|
| `ZENROWS_API_KEY` | backend (`zenrows.api_key`) | Yes | Auth key for ZenRows scraping requests | `ZENROWS_API_KEY=your_api_key_here` |

### Optional

| Variable | Required | Purpose |
|---|---|---|
| `API_BASE_URL` (Flutter `--dart-define`) | No | Frontend API/docs base URL override; empty means same-origin relative paths |
| `CORS_ALLOWED_ORIGIN_PATTERNS` (Spring relaxed binding for `cors.allowed-origin-patterns`) | No | Override allowed CORS origin patterns for `/api/**` |

Notes:

- Root `.env` is referenced by `docker-compose.yml` for the backend service.
- `.env` is gitignored; keep secrets out of source control.

## Quick Start (Docker)

1. Create/update root `.env`:

```env
ZENROWS_API_KEY=your_api_key_here
```

2. Start everything:

```bash
docker compose up --build
```

3. Access services:

- Frontend: `http://localhost`
- Backend API: `http://localhost:8080/api/linkedin`
- Swagger UI: `http://localhost/swagger-ui.html`
- OpenAPI JSON: `http://localhost/api-docs`

4. Stop:

```bash
docker compose down
```

## Local Development (Backend)

### Backend

Set `ZENROWS_API_KEY`, then run:

**PowerShell (Windows)**

```powershell
$env:ZENROWS_API_KEY="your_api_key_here"
.\scraper-backend\mvnw.cmd -f .\scraper-backend\pom.xml spring-boot:run
```

**bash (macOS/Linux)**

```bash
export ZENROWS_API_KEY="your_api_key_here"
cd scraper-backend
./mvnw spring-boot:run
```

## Documentation

- [API reference](docs/api.md)
- [Architecture and project structure](docs/architecture.md)
- [Technical approach](docs/technical-approach.md)
- [Known limitations and security notes](docs/limitations-and-security.md)
