# Prototype: "Time travel" between article versions

Status: **prototype / proof of concept** — gated behind `usePrivateFeaturesFlag()`, no automated tests, not ready for production use.

## Scope

On the norm-article detail page (`[eId].vue`), a single article (`§`) of one dated wording ("Fassung"/expression) of a norm is shown, along with its own validity interval ("Gültig ab/bis"). That interval can span several expressions of the norm if the article's text didn't change across amendments — but until now there was no way to actually navigate from the article being read to the same article in an earlier or later Fassung.

This prototype adds a "Andere Fassungen dieses Paragrafen" list below the article, showing every version of the norm together with a link to the same article (matched by `eId`) in that version. It was built in two iterations:

1. A flat, per-version list of links (one row per expression of the norm).
2. Grouping of consecutive versions where the article's text is unchanged, plus explicit "this article doesn't exist in this version" detection, so the list reflects genuine content changes rather than every unrelated amendment to the norm.

Out of scope: renumbering detection (an article that changes eId across versions), a backend endpoint purpose-built for this (all lookups are done from the frontend against existing endpoints), and any UI beyond a plain list.

## Files touched

- `frontend/src/components/documents/norms/ArticleVersionList.vue` (new) — all prototype logic lives here.
- `frontend/src/utils/norm.ts` — added `findPartByEId`, a recursive search over a `hasPart` tree.
- `frontend/src/pages/norms/eli/.../[eId].vue` — wires the new component in, fetching all versions via the existing `useNormVersions` composable.

## Approach

### Data model recap

- Route: `/eli/:jurisdiction/:agent/:year/:naturalIdentifier/:pointInTime/:version/:language/:eId`. `jurisdiction/agent/year/naturalIdentifier` identify the **work** (the abstract norm); `pointInTime/version/language` identify one **expression** (a specific wording valid from a date); `eId` identifies one article/part within that expression's `hasPart` tree.
- `LegislationExpression.legislationIdentifier` is a full string, e.g. `eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu`. Splitting on `/` and taking the last three segments gives `[pointInTime, version, language]` — this is how a link to "the same article, different version" is constructed, by swapping those three route params and keeping `eId`.
- `useNormVersions(workEli)` (pre-existing composable, `frontend/src/composables/useNormVersions.ts`) returns every expression of a work via `GET /v1/legislation/work-example/{workEli}`. This list is lightweight (`LegislationExpressionSearchSchema`-shaped) and does **not** include each expression's `hasPart` tree.
- To know whether/how an article changed *within* a given expression, the full metadata endpoint `GET /v1/legislation/eli/{expressionEli}` is needed — this does return `hasPart`, including each part's own `temporalCoverage`.

### Fetching strategy

Because the version-list endpoint doesn't carry per-article data, the component fetches full metadata for **every version of the work, in parallel**, client-side only (`import.meta.client`), to look up the current `eId` in each one's `hasPart` tree. This is an N+1 pattern (N = number of expressions of the work) — deliberately accepted for a prototype, see [Limitations](#limitations-known-issues).

## High-level logic

### 1. Existence check: `findPartByEId`

```ts
function findPartByEId(parts, eId) {
  for (const part of parts) {
    if (decodeURIComponent(part.eId) === eId) return part;
    const found = findPartByEId(part.hasPart, eId);
    if (found) return found;
  }
  return undefined;
}
```

A plain recursive DFS over `hasPart`, at any depth (not just leaf articles, since tree shape can differ between expressions — e.g. a section might get split or merged). Returns the matching part, or `undefined` if the `eId` isn't present in that expression at all.

This was verified to be a **complete** existence check for this system: the `eId` coverage of `hasPart` was confirmed (by tracing the backend's OpenSearch table-of-contents mapper and the single-article HTML endpoint's XSLT matching logic) to be exactly the same set of elements — article, attachment, preamble/conclusion formula — that the article-content endpoint can resolve. No deeper content fetch is needed to answer "does this eId exist in version X".

### 2. Per-version info: `loadArticleVersionInfo`

For each version, once its full metadata has been fetched:

```ts
const matchedPart = findPartByEId(metadata.hasPart, eId);
info[version.legislationIdentifier] = matchedPart
  ? { temporalCoverage: matchedPart.temporalCoverage, exists: true }
  : { temporalCoverage: version.temporalCoverage, exists: false };
```

- If found: store the **article's own** `temporalCoverage` (its validity interval, which is what may span several expressions unchanged).
- If not found: mark `exists: false`, with the whole expression's `temporalCoverage` kept only as a fallback value (not used for display in this case).
- On fetch error: optimistically assume `exists: true` with the expression's own `temporalCoverage`, so a transient network failure doesn't wrongly report an article as removed.
- Until a version's fetch resolves, the same optimistic default (`exists: true`, expression-level `temporalCoverage`) is used, so the list renders immediately and progressively refines as data arrives, rather than blocking on 100+ requests before showing anything.

### 3. Grouping: `groupedVersions`

Versions are first sorted descending by `temporalCoverage` (newest first), then folded into groups in a single pass:

- A **grouping key** is computed per version: the article's own `temporalCoverage` string if it exists in that version, or the sentinel `"not-found"` if it doesn't.
- Consecutive versions (in the sorted order) with the same key are merged into one group. For existing articles this means: *the exact interval during which the text was unchanged*, which is authoritative because `temporalCoverage` on the article part is set by the backend precisely to reflect that. For "not found" runs, grouping is done purely by contiguity — any run of consecutive versions without the article collapses into one entry, regardless of how the underlying expressions' own dates differ, since "not present" is the only claim being made.
- Each group tracks its earliest and latest member (by construction of the sorted, single-pass fold): the **earliest** version is the one that introduced the current wording (or the start of an absence), and is used both as the group's "Gültig ab" anchor and as the link target — since any member of an "unchanged" group is content-equivalent, linking to the version that introduced it is the most meaningful choice. The **latest** version's own `temporalCoverage.to` is used only for displaying an approximate range on "not found" groups, which have no single meaningful interval of their own.
- A group is flagged `containsCurrent` if any of its members is the expression currently being viewed — that group is rendered as plain text ("Aktuelle Fassung") instead of a link.

### 4. Rendering

- `exists: false` groups render as **"Paragraf existiert in dieser Fassung nicht"**, with no link — this eliminates the class of dead links that would otherwise 404 against the backend's article-content endpoint.
- `exists: true`, non-current groups render as a "Zur Fassung" link, built by swapping `pointInTime`/`version`/`language` (parsed from the group's earliest member's `legislationIdentifier`) into the current route's params, keeping the same route name and `eId`.
- `exists: true` groups spanning more than one version show a count, e.g. *"(93 Fassungen, Paragraf unverändert)"*.

## Learnings and limitations

### What worked well

- **`hasPart` is sufficient for existence checks.** No need to fetch or diff actual article HTML/text — the metadata tree alone answers "does this eId exist here", confirmed both by tracing backend code and by testing against real e2e data.
- **The article's own `temporalCoverage` is already the "unchanged" signal.** No manual content diffing was needed to detect that an article is unchanged across versions — the backend already computes and exposes exactly this interval per article part; the prototype just needed to read and group by it.
- **Validated against real data, not just synthetic cases.** Testing against the Sozialgerichtsgesetz (`eli/bund/bgbl-1/1953/s1239`, 121 expressions) surfaced a genuine real-world case: `§ 10` existed, was removed for two consecutive expressions (2004-08-06 to 2004-12-14), then reappeared. This was cross-checked directly against the raw `hasPart` data for those specific expressions and confirmed correct — a good sign the logic generalizes beyond the cases it was designed around.

### Known issues / limitations

- **`eId` is the only identifier available, and it is not a stable cross-version identifier.** Per the schema's own doc comment, `eId` is only guaranteed unique *within one expression*. This cuts both ways:
  - **False negatives**: a provision that gets renumbered (e.g. § 9 → § 9a) will appear to "not exist" in later versions, even though it logically continues to exist — not fixable with `eId` matching alone.
  - **False positives**: if an `eId` like `art-z10` is later reused (decades apart) for an unrelated provision, the current logic will happily group it as "unchanged" purely because the `eId` and `temporalCoverage` line up — there is no content comparison to catch this. This is a real, observed risk given how German norm numbering evolves, not just a theoretical one.
  - There **is** a more stable identifier in the raw Akoma Ntoso XML — a `GUID` attribute per element, clearly intended for exactly this kind of cross-version tracking. The backend already parses it into the `Article` OpenSearch model (`NormLdmlToOpenSearchMapper.java`), but it is dropped before reaching any API (`LegislationExpressionPartSchema`/`hasPart` don't carry it). Exposing it would be the "correct" long-term fix, but requires a schema/API change, and its real-world stability (vs. only having been checked on hand-authored fixtures) is unverified.
- **N+1 fetch pattern.** The component fetches full metadata for every version of the work in parallel, purely to read each one's `hasPart`. For norms with many expressions (the SGG test case has 121), this means 100+ background HTTP requests per article page view. Acceptable for a private-flag prototype; would need a dedicated, purpose-built backend endpoint (e.g. "all `hasPart` entries matching this `eId` across a work's expressions") before this could ship broadly.
- **No loading/empty/error UI.** The section simply doesn't render until the version list itself has loaded successfully; per-version grouping data fills in progressively and silently as each fetch resolves, with no spinner or partial-state indicator.
- **No tests.** Consistent with the prototype/PoC framing; would need coverage of the grouping algorithm (in particular the "not-found" contiguity grouping and the optimistic-default-while-loading behavior) before productionizing.

### Tradeoffs made deliberately

- Chose **eId-string matching** over investing in exposing the `GUID` field, since the goal was a fast prototype demonstrating the UX, not a production-grade identifier scheme.
- Chose **client-side, per-version fetching** over a new backend endpoint, to keep the change contained to the frontend for this iteration.
- Chose to **link to the earliest version in a group** rather than, say, the version closest to the one being viewed — makes the "Gültig ab" date and the link target consistent, at the cost of sometimes navigating further back in time than strictly necessary when clicking from the middle of a long unchanged period.
