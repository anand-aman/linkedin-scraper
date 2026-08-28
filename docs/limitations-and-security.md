# Known Limitations

- Scraping quality depends on ZenRows upstream response quality and LinkedIn page variability.
- If `ZENROWS_API_KEY` is missing/invalid, scraping requests fail.
- Only one scrape endpoint is exposed (`POST /api/linkedin`); no async queueing/persistence layer is implemented.
- No built-in rate limiting, retry orchestration, or caching layer is implemented.
- Output schema differs by `type` mode (`hybrid`, `html`, `parsed`) rather than a single strict universal schema.
- The implementation currently focuses on fields listed in DTOs/services and does not provide dedicated skills/certifications fields.
- Public HTTPS hosting/deployment configuration is not part of this repository; it must be provided by your target runtime/platform.

# Security Notes

- Do not commit secrets.
- Keep `ZENROWS_API_KEY` in environment variables (or root `.env` for local compose) only.
- `.env` is already excluded via `.gitignore`.
