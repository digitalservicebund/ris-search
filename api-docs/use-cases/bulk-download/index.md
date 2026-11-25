---
title: Download documents in bulk
---

# Download documents in bulk

**Goal:** Get all documents of one kind, e.g. for data analysis, research or training language models.

**Endpoint:** `GET /v1/bulk-zip-links`

## Why not page through the search?

Search results are limited to 10,000 per query, and requests are limited to 600 per minute (see [Rate limiting](/guides/rate-limiting/)). For complete datasets, use the bulk ZIP files.

## 1. Get the download links

```bash
curl -s https://testphase.rechtsinformationen.bund.de/v1/bulk-zip-links \
  | jq '.dataSet[] | {name, url: .distribution.contentUrl}'
```

The response lists one dataset per document kind: `legislation`, `rechtsprechung`, `literature` and `administrative-directives`.

## 2. Download the ZIP

```bash
URL=$(curl -s https://testphase.rechtsinformationen.bund.de/v1/bulk-zip-links \
  | jq -r '.dataSet[] | select(.name=="rechtsprechung") | .distribution.contentUrl')
curl -o rechtsprechung.zip "$URL"
```

## What is in the ZIP

- A snapshot of all documents of that kind as they were when the snapshot was created: XML plus related files such as attachments.
- The files are stored flat.
