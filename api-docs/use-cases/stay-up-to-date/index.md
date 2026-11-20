---
title: Stay up to date
---

# Stay up to date

**Goal:** Keep a local copy in sync without downloading everything again.

**Endpoints:** one changelog per document kind

| Document kind             | Changelog                                |
| ------------------------- | ---------------------------------------- |
| Legislation               | `/v1/legislation/changelog`              |
| Case law                  | `/v1/rechtsprechung/changelog`           |
| Literature                | `/v1/literature/changelog`               |
| Administrative directives | `/v1/administrative-directive/changelog` |

## Recommended workflow

1. Download the full dataset once, see [Download everything](/use-cases/bulk-download/). Note the time.
2. On a regular basis (e.g. daily), request the changes since your last run.
3. Download each changed document from its `contentUrl`, and delete each document listed in `deleted`.

## Request the changes

`from` and `to` are required, in ISO 8601 date-time format with time zone.

```bash
curl -G https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung/changelog \
  --data-urlencode 'from=2026-11-09T00:00:00Z' \
  --data-urlencode 'to=2026-11-10T00:00:00Z'
```

## What you get back

```json
{
  "changed": [{ "@id": "…", "@type": "…", "contentUrl": "…" }],
  "deleted": [{ "@id": "…", "@type": "…" }],
  "allChanged": false
}
```

| Field        | Meaning                                                                   |
| ------------ | ------------------------------------------------------------------------- |
| `changed`    | Documents that were added or changed. Get them from `contentUrl`.         |
| `deleted`    | Documents that were removed. Delete them locally.                         |
| `allChanged` | `true` if the whole dataset was rebuilt. Download the full dataset again. |

## Pitfalls

- Let consecutive time windows overlap a little (e.g. start a few minutes before your last `to`). Processing a document twice does no harm, missing it does.
- Always check `allChanged` before processing the lists.
- A time zone offset like `+01:00` must be URL-encoded (`%2B01:00`). `--data-urlencode` does this for you. Using UTC (`Z`) avoids the problem.
