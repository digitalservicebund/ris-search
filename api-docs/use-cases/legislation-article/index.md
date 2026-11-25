---
title: Get a single article (§)
---

# Get a single article (§)

**Goal:** Get one article, e.g. "§ 3", instead of the whole law.

::: tip **Info**
Only currently valid laws are available on Testphase. We expect to add past and future versions of legislation in early 2028.
:::

## 1. Find the version of the law

Get the expression ELI as described in [Get a specific version of a law](/use-cases/legislation-version/), for example `eli/bund/bgbl-1/2011/s98/2026-10-14/1/deu`.

## 2. Look up the article in `hasPart`

The metadata of a version lists its structure in `hasPart`. Each part has a display name (`name`, e.g. `§ 3`), a technical ID (`eId`) and its own `encoding` with links. Parts can be nested (e.g. book → section → article), so search recursively:

```bash
curl -s https://testphase.rechtsinformationen.bund.de/v1/legislation/eli/bund/bgbl-1/2011/s98/2026-10-14/1/deu \
  | jq '.. | objects | select(.name? == "§ 3") | {eId, headline, url: .encoding[0].contentUrl}'
```

## 3. Get the article as HTML

```bash
curl https://testphase.rechtsinformationen.bund.de/v1/legislation/eli/bund/bgbl-1/2011/s98/2026-10-14/1/deu/2026-11-03/regelungstext-1/{eId}.html
```

Replace `{eId}` with the value from step 2.
