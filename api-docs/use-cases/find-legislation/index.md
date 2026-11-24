---
title: Find legislation
---

# Find legislation

**Goal:** Find legislation by keyword or abbreviation, optionally only the ones in force today.

::: tip **Info**
Only currently valid laws are available on Testphase.
:::

**Endpoint:** `GET /v1/legislation`

## 1. Search by keyword

All terms must occur in the document. Put the terms in double quotes to search for an exact phrase.

```bash
curl -G https://testphase.rechtsinformationen.bund.de/v1/legislation \
  --data-urlencode 'searchTerm=Kakao Schokolade'
```

## 2. Filter by abbreviation

```bash
curl -G https://testphase.rechtsinformationen.bund.de/v1/legislation \
  --data-urlencode 'abbreviation=KakaoV 2003'
```

## 3. Get only one current version per law

A law usually has many versions (expressions), and a plain search returns all of them. `mostRelevantOn` returns exactly one version per law: the one in force on that date. If none is in force on that date, you get the next upcoming version, or else the most recent one.

```bash
curl -G https://testphase.rechtsinformationen.bund.de/v1/legislation \
  --data-urlencode 'searchTerm=Kakao' \
  --data-urlencode 'mostRelevantOn=2026-11-10'
```

## What you get back

Each entry in `member[].item` is one version of a law:

```bash
curl -sG https://testphase.rechtsinformationen.bund.de/v1/legislation \
  --data-urlencode 'searchTerm=Kakao' \
  | jq '.member[].item | {name, abbreviation, legislationIdentifier, temporalCoverage, legislationLegalForce}'
```

| Field | Meaning |
| --- | --- |
| `name` | Official long title (Amtliche Langüberschrift) |
| `abbreviation` | Official abbreviation, or the RIS abbreviation if there is no official one |
| `legislationIdentifier` | ELI of this version (expression), e.g. `eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu` |
| `exampleOfWork.legislationIdentifier` | ELI of the law independent of its versions (work), e.g. `eli/bund/bgbl-1/1975/s1760` |
| `temporalCoverage` | Validity period, e.g. `1998-02-06/..` (open end) |
| `legislationLegalForce` | Whether this version is currently in force |
| `encoding[].contentUrl` | Links to HTML and XML of this version |

## Pitfalls

- `dateFrom` and `dateTo` filter by “Ausfertigungsdatum”, not by validity. To filter by validity, use `temporalCoverageFrom` and `temporalCoverageTo` or `mostRelevantOn`.
- Results are sorted by relevance. Use `sort=-date` for newest first, or `sort=temporalCoverageFrom`.
- You can page through at most 10,000 results. To get everything, see [Download everything](/use-cases/bulk-download/).
