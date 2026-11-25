---
title: Get a specific version of a law
---

# Get a specific version of a law

**Goal:** Get the text of a law as it was valid on a given date.

::: tip **Info**
Only currently valid laws are available on Testphase. We expect to add past and future versions of legislation in early 2028.
:::

## How versions are identified (ELI)

Every law is identified by a [European Legislation Identifier (ELI)](/standards/). The ELI has three levels:

| Level | Meaning | Example |
| --- | --- | --- |
| Work | The law, independent of its versions | `eli/bund/bgbl-1/1975/s1760` |
| Expression | One version of the law: date it takes effect, version number, language | `eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu` |
| Manifestation | One file of that version, e.g. the main text | `…/1998-01-29/10/deu/1998-01-29/regelungstext-1.xml` |

## 1. List all versions of a law

Filter by the work ELI. You can find it in any search result under `exampleOfWork.legislationIdentifier`.

```bash
curl -sG https://testphase.rechtsinformationen.bund.de/v1/legislation \
  --data-urlencode 'eli=eli/bund/bgbl-1/1975/s1760' \
  --data-urlencode 'sort=temporalCoverageFrom' \
  | jq '.member[].item | {legislationIdentifier, temporalCoverage}'
```

## 2. Pick the version valid on a date

Set `temporalCoverageFrom` and `temporalCoverageTo` to the same day.

```bash
curl -sG https://testphase.rechtsinformationen.bund.de/v1/legislation \
  --data-urlencode 'eli=eli/bund/bgbl-1/1975/s1760' \
  --data-urlencode 'temporalCoverageFrom=2020-06-19' \
  --data-urlencode 'temporalCoverageTo=2020-06-19' \
  | jq '.member[].item.legislationIdentifier'
```

## 3. Get metadata and table of contents of that version

```bash
curl https://testphase.rechtsinformationen.bund.de/v1/legislation/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu
```

The response contains `hasPart` (the structure of the law down to single articles) and `encoding` (the available files).

## 4. Get the full text

Use the links from `encoding[].contentUrl` instead of building the URL yourself:

```bash
curl -s https://testphase.rechtsinformationen.bund.de/v1/legislation/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu \
  | jq -r '.encoding[] | "\(.encodingFormat)  \(.contentUrl)"'
```

```bash
# HTML
curl https://testphase.rechtsinformationen.bund.de/v1/legislation/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu/1998-01-29/regelungstext-1.html
# XML (LegalDocML.de)
curl https://testphase.rechtsinformationen.bund.de/v1/legislation/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu/1998-01-29/regelungstext-1.xml
```

## Pitfalls

- The date in the manifestation part of the URL can differ from the expression date. Always take the URL from `encoding`.
- `regelungstext-1` is the main text. Some laws have further files such as attachments (`anlage-…`). The ZIP of a manifestation contains all of them, see [Choose a format](/use-cases/formats/).
