---
title: Get a single article (§)
---

# Get a single article (§)

**Goal:** Get one article, e.g. "§ 3", instead of the whole law.

::: info
Only currently valid laws are available on Testphase.
:::

## 1. Find the version of the law

Get the expression ELI as described in [Get a specific version](/use-cases/legislation-version/), for example `eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu`.

## 2. Look up the article in `hasPart`

The metadata of a version lists its structure in `hasPart`. Each part has a display name (`name`, e.g. `§ 3`), a technical ID (`eId`) and its own `encoding` with links. Parts can be nested (e.g. book → section → article), so search recursively:

```bash
curl -s https://testphase.rechtsinformationen.bund.de/v1/legislation/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu \
  | jq '.. | objects | select(.name? == "§ 3") | {eId, headline, url: .encoding[0].contentUrl}'
```

## 3. Get the article as HTML

```bash
curl https://testphase.rechtsinformationen.bund.de/v1/legislation/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu/1998-01-29/regelungstext-1/{eId}.html
```

Replace `{eId}` with the value from step 2.

## Pitfalls

- Single articles are only available as HTML. For XML, get the complete `regelungstext-1.xml` and select the element with that `eId`.
- Match on `name` (`§ 3`), not on the position in the list. Don't rely on the order of `hasPart` to follow the numbering of the articles.
- Look up the `eId` again for each version of the law instead of reusing it from another version.
