# Prototype: "Time travel" between article versions (doknr-based)

Status: **prototype / proof of concept** — gated behind `usePrivateFeaturesFlag()`, no automated tests, not ready for production use.

## Scope

On the norm-article detail page (`[eId].vue`), a single article (`§`) of one dated wording ("Fassung"/expression) of a norm is shown. Until now there was no way to navigate from the article being read to the same article in an earlier or later Fassung. This prototype adds a "Andere Fassungen dieses Paragrafen" list below the article, showing every version of the norm together with a link to the same provision in that version.

The interesting part isn't the list itself, but *how "the same provision" is identified across versions*. An `eId` (e.g. `art-z3`) is only guaranteed unique **within one expression** — it's not a stable cross-version identifier. This prototype instead uses `ris:doknr`, a per-element identifier already present in the raw LegalDocML XML, which turns out to be much better suited to the job. Two capabilities fall out of that:

1. Group consecutive versions where an article's text is unchanged, so the list reflects genuine content changes rather than every unrelated amendment to the norm.
2. Correctly follow a provision even if its `eId` changes between versions (renumbering) — something a pure `eId`-matching approach structurally cannot do.

Out of scope: any backend changes (this is deliberately frontend-only, see [Approach](#approach)), and a UI beyond a plain list.

## Files touched

- `frontend/src/components/documents/norms/ArticleVersionList.vue` (new) — all prototype logic lives here.
- `frontend/src/utils/norm.ts` — added `parseDoknrMap` and `getDoknrStableId`.
- `frontend/src/pages/gesetze/eli/.../[eId].vue` — wires the new component in, fetching all versions via the existing `useNormVersions` composable.

## Approach

### The `ris:doknr` identifier

Every norm expression's raw LegalDocML XML embeds a flat lookup table of entries like:

```xml
<ris:doknr source="#art-z3">BJNR261510016BJNE000301116</ris:doknr>
```

`source` is a fragment reference to the element's `eId`. The `doknr` value splits into two parts:

- The **first 21 characters** (`BJNR261510016BJNE0003`) reliably identify "the same provision" across every expression of the work — confirmed empirically against this branch's e2e data (`eli/bund/bgbl-1/2016/s2615`, 13 expressions of the same work): this prefix was byte-identical for a given article across all versions checked.
- The **remaining characters** (`01116`) are a version counter that only changes when that specific provision's content changed. Verified directly: for `§ 3` and `§ 7` of that norm, the counter steps `00000 → 01116 → 02130` exactly at the expressions where those articles were actually amended, while untouched siblings (e.g. `§ 4`, for most of its history) keep their counter unchanged across the same expressions.

This table exists at the same granularity as the structural table of contents (`hasPart` in the JSON API) — one entry per article, section/`abschnitt` container, and preamble/conclusion formula, not per paragraph or sentence — confirmed by cross-checking the sets of `eId`s appearing in each.

### Why frontend-only works here

No backend code parses or exposes `doknr` today (`grep -r doknr backend/src/main/java` returns nothing) — so this had to either be a backend change, or read the XML directly. It turned out no backend change was needed:

- The raw XML is already served publicly, unauthenticated, per expression, via the existing manifestation endpoint (`.../{pointInTimeManifestation}/{subtype}.xml`).
- Its URL is already present in the **lightweight version-list response** the frontend already fetches (`useNormVersions(workEli)` → `GET /v1/legislation/work-example/{eli}`) — that response's `encoding` field includes a working `application/xml` `contentUrl` for every version, gettable via the pre-existing `getManifestationUrl(version, "application/xml")` utility.

So the whole feature is: fetch each version's XML as plain text (reusing the same `$risBackend` fetch-as-text pattern already used elsewhere for HTML article content) and regex-extract the small `ris:doknr` table from it — no DOM/XML parser, no new dependency, no backend endpoint.

The honest cost: fetching the *entire* raw XML file per version, just to read a handful of small tags near the top, is wasteful — there's no backend range-request support to avoid downloading the whole document. Accepted deliberately for a frontend-only prototype; a small backend endpoint (e.g. "give me the doknr table for these expressions") would be the efficient long-term fix.

## High-level logic

### 1. Fetching: one doknr map per version

```ts
const doknrMaps = ref<Record<string, Map<string, string>>>({}); // legislationIdentifier -> (eId -> doknr)

async function loadDoknrMap(version) {
  const xmlUrl = getManifestationUrl(version, "application/xml");
  const xml = await $risBackend<string>(xmlUrl, { headers: { Accept: "application/xml" } });
  doknrMaps.value[version.legislationIdentifier] = parseDoknrMap(xml);
}
```

Run in parallel for every version of the work, client-side only (`import.meta.client`) since it's a progressive UX enhancement layered on top of an already-rendered page, not needed for SSR/first paint. `parseDoknrMap` is a plain regex scan (`<ris:doknr source="#([^"]+)">([^<]+)</ris:doknr>`) over the raw XML text — no DOM parsing.

### 2. Reference point: the current article's stable id

The current expression is itself one of the fetched versions, so once its map has loaded, the current article's own doknr can be read out of it directly:

```ts
const currentDoknr = computed(() => doknrMaps.value[currentLegislationIdentifier]?.get(eId));
const stableId = computed(() => currentDoknr.value ? getDoknrStableId(currentDoknr.value) : undefined);
```

Nothing else can be computed until this resolves — grouping is gated on `stableId` being available.

### 3. Matching by stable id, not by eId

For each version, instead of looking up `eId` directly (which is what the eId-based approach did, and which breaks under renumbering), every entry in that version's doknr map is scanned for one whose doknr **starts with** the reference stable id:

```ts
function findMatch(version) {
  for (const [eId, doknr] of doknrMaps.value[version.legislationIdentifier]) {
    if (doknr.startsWith(stableId.value)) return { eId, doknr };
  }
  return undefined;
}
```

This is the core improvement: it returns the *correct `eId` for that specific version*, which may differ from the current page's `eId`. If no entry matches, the provision genuinely doesn't exist in that version.

### 4. Grouping

Versions are sorted descending by `temporalCoverage` (newest first) and folded into groups in one pass, keyed by the **full matched doknr** (or the sentinel `"not-found"`): consecutive versions with an identical doknr are the same content, since the version counter is authoritative about whether that exact provision's text changed. Each group tracks its oldest member (`earliestVersion`, used as the link target and "Gültig ab" anchor) and newest member (`latestVersion`, used for "Gültig bis"). Versions whose XML fetch hasn't resolved yet are skipped and simply appear once they do (progressive rendering — there's no secondary fallback signal to render an interim guess with, unlike the previous `temporalCoverage`-based iteration).

### 5. Display and links

- Groups with a match render as "Zur Fassung" (or "Aktuelle Fassung" as plain text if the group contains the currently-viewed expression), linking via the group's `earliestVersion` **and its own matched `eId`** — swapping `pointInTime`/`version`/`language` from that expression's `legislationIdentifier` into the current route while keeping the route name.
- Groups with no match render as "Paragraf existiert in dieser Fassung nicht", with no link.
- Every group's displayed date range is derived from its constituent expressions' own `temporalCoverage` (earliest's start, latest's end) — there's no `hasPart`-derived article-level interval available in this XML-only approach, so this replaces what the prior eId-based iteration used.

## Learnings and limitations

### What worked well

- **`doknr` is a materially better signal than `eId`.** It's keyed by content identity, not by position/label, so it naturally handles both directions of the old approach's weakness: it won't falsely merge an `eId` that gets reused for unrelated content, and (structurally, even if not yet observed in this dataset) it can follow a provision whose `eId` changes.
- **Validated against real, not synthetic, data.** Every claim in this document — the 21/5 character split, the counter stepping only on real amendments, the doknr table's element coverage matching `hasPart` — was checked directly against actual e2e XML fixtures (`eli/bund/bgbl-1/2016/s2615`, 13 expressions) and cross-checked against the live backend, including verifying that group member counts sum exactly to the work's total expression count (12 + 1 = 13 for an unamended article; 1 + 9 + 3 = 13 for one amended twice).
- **Genuinely frontend-only.** No backend or schema change was needed — the raw XML and its URL were both already reachable from data the frontend already fetches.

### Known issues / limitations

- **Full-document fetch per version.** Each version's entire XML file is downloaded just to read a small metadata table near the top. Fine for a handful of versions (the tested norm has 13); would be wasteful for a norm with 100+ expressions. No backend range-request support exists to avoid this — a purpose-built endpoint would be the real fix.
- **Regex-based XML parsing.** Robust against every sample checked (consistent, simple, unescaped tag structure), but it's pattern matching on text, not schema-validated parsing — a structurally unusual document could break it silently rather than failing loudly.
- **Renumbering resilience is inferred, not directly observed.** The mechanism (matching by stable-id prefix rather than eId) should handle a provision whose `eId` changes between versions, but no such case was present in the dataset used to verify this prototype — worth a dedicated check against a norm known to have renumbered a provision before relying on this claim.
- **No loading/empty/error UI.** The section doesn't render until the current version's doknr has resolved; other versions' groupings fill in silently and progressively as their fetches complete.
- **No tests.** Consistent with the prototype/PoC framing.

### Tradeoffs made deliberately

- Chose **regex text extraction** over parsing the XML into a DOM, since the only thing needed is one small, consistently-shaped tag — avoids pulling in an XML parsing dependency for a prototype.
- Chose to **fetch the whole XML file per version** rather than invest in a backend endpoint, per the explicit "can this be done frontend-only" framing for this iteration — bandwidth efficiency was consciously deprioritized.
- Chose to **link using each version's own matched `eId`** rather than reuse the current page's `eId` (unlike the prior iteration) — slightly more logic, but directly closes the renumbering gap that was the main known weakness going in.
