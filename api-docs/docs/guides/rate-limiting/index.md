---
title: Rate Limiting
hero:
  title: Rate Limiting
  text: Learn about rate limits, how to avoid exceeding them, and what to do if you do exceed them.
---

## Rate Limiting

To ensure fair usage and protect our infrastructure from abuse, we enforce rate limits on incoming requests.

### Rate Limit Policy
- **Requests Per Minute (RPM):** A maximum of **600 requests per minute** per client IP is allowed.
- Requests exceeding this limit may receive a **503 Service Unavailable** response.

### Best Practices to Avoid Rate Limiting
- Implement **exponential backoff**.
- Optimize API usage by **caching** responses where applicable.