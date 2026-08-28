# Implementation Notes (Technical Approach)

- The backend depends on ZenRows as the upstream fetch/extraction provider.
- ZenRows response fields used:
  - `html` (raw HTML returned by provider)
  - `parsed` object fields (member/current_position/experience/posts/languages)
- `HtmlScraperService` parses HTML using:
  - CSS selectors for top-card basics/images
  - JSON-LD blocks for person/article/post metadata
  - anchor extraction for unique links
- `HybridScraperService` performs:
  - field-level preference/fallback selection
  - post/article/experience/education deduplication
  - education-vs-experience correction heuristics using keyword hints
