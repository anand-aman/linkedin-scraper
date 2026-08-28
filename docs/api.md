# API Documentation

Base path: `/api`

## `POST /api/linkedin`

Scrapes a LinkedIn public profile URL and returns one of three response schemas based on `type`.

### Query parameters

| Parameter | Required | Allowed values | Default | Behavior |
|---|---|---|---|---|
| `type` | No | `hybrid`, `html`, `parsed` | `hybrid` | Chooses parser/response mode |

- `hybrid`: merged response from parsed payload + HTML extraction with deduping/fallbacks
- `html`: HTML/JSON-LD extraction response
- `parsed`: normalized mapping from ZenRows parsed payload

### Request body

```json
{
  "url": "https://www.linkedin.com/in/example/"
}
```

### cURL examples

**Default (`hybrid`)**

```bash
curl -X POST "http://localhost:8080/api/linkedin" \
  -H "Content-Type: application/json" \
  -d '{"url":"https://www.linkedin.com/in/example/"}'
```

**Explicit `html` mode**

```bash
curl -X POST "http://localhost:8080/api/linkedin?type=html" \
  -H "Content-Type: application/json" \
  -d '{"url":"https://www.linkedin.com/in/example/"}'
```

**Explicit `parsed` mode**

```bash
curl -X POST "http://localhost:8080/api/linkedin?type=parsed" \
  -H "Content-Type: application/json" \
  -d '{"url":"https://www.linkedin.com/in/example/"}'
```

### Success response shapes (summary)

- **`hybrid`**: `profileUrl`, `vanityUrl`, `fullName`, `headline`, `description`, `currentPosition`, `location`, `countryCode`, `followerCount`, `connectionCount`, `profilePhotoUrl`, `coverImageUrl`, `languages[]`, `posts[]`, `articles[]`, `experiences[]`, `education[]`
- **`parsed`**: `profileUrl`, `vanityUrl`, `fullName`, `headline`, `currentPosition`, `location`, `countryCode`, `followerCount`, `connectionCount`, `profilePhotoUrl`, `coverImageUrl`, `languages[]`, `posts[]`, `experiences[]`
- **`html`**: `name`, `profileUrl`, `description`, `location`, `profileImage`, `coverImage`, `followers`, `experience[]`, `education[]`, `articles[]`, `posts[]`, `links[]`

### Error responses

Errors use a standard payload:

```json
{
  "timestamp": "2026-08-28T14:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Malformed request",
  "path": "/api/linkedin"
}
```

Typical statuses from `GlobalExceptionHandler`:

- `400` invalid `type`, malformed JSON, missing/invalid request data
- `502` upstream ZenRows issues (e.g., missing API key, request/parsing failures)
- `500` unexpected internal errors
