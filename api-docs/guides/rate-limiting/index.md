---
title: Rate limiting
---

# Rate limiting

To ensure fair usage and protect our infrastructure from abuse, we enforce rate limits on incoming requests.

## Rate limit policy

- **Requests per minute (RPM):** A maximum of **600 requests per minute** per client IP is allowed.
- Requests exceeding this limit may receive a **503 Service Unavailable** response.

## Best practices to avoid rate limiting

- Implement **exponential backoff**.
- Optimize API usage by **caching** responses where applicable.
