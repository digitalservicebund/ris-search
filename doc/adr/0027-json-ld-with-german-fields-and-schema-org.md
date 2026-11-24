# 27. JSON-LD with German fields and schema.org mappings

Date: 2026-11-11

## Status

Proposed

## Context

At the moment our API responses use JSON-LD, but not consistently. A single hand-written
context describes all document kinds. Some fields in the responses are missing
from it, and some types in the context have no field mappings at all. Field
names mix English and German. The `ris:` prefix points to a vocabulary
that is not published anywhere.

In a previous meeting we agreed that if we keep JSON-LD, we need a strategy
for it, together with schema.org, and a way to maintain it in the future. This
ADR records what we decided in the dev exchange on 2026-08-05.

[ADR 24](0024-use-german-for-domain-specific-terms-in-code.md) already says
that we use German for domain terms. The case law endpoint already uses German
field names.

## Decision

### Keep JSON-LD

We keep JSON-LD in the API responses and make every response valid JSON-LD.

### German paths and fields

All endpoint paths and response fields use German names. Where possible, a
field is named after its element in the LDML (e.g. `entscheidungsdatum` for
`<ris:entscheidungsdatum>`), so that the API and the documents use the same terms.
Fields without a matching LDML element get a German name chosen by the team.

The LDML element names are only the starting point. If an element is renamed
in the LDML later, the API field keeps its name. How we change and version the
API is decided in [ADR 29](0029-no-api-versioning-until-go-live.md).

### schema.org where possible

We map fields to schema.org where a suitable type or property exists:

- Norms map to [`schema:Legislation`](https://schema.org/Legislation).
- For the other document kinds, we still check whether
  [`schema:CreativeWork`](https://schema.org/CreativeWork) fits. If it doesn't,
  these document kinds use their own types with the `ris:` prefix.

Fields without a suitable schema.org property use the `ris:` prefix.

The mapping happens in the JSON-LD context, not in the field names. The API
fields keep their German names, and the context maps them to the English
schema.org terms, for example:

    "ausfertigungsdatum": "schema:legislationDate"

### Our own `ris:` vocabulary

Terms with the `ris:` prefix form our own vocabulary that describes the API
responses. Where possible, we use the descriptions from the glossary.

How we maintain and publish this vocabulary needs to be decided separately.
Until then, the `ris:` IRIs don't resolve. Once published, they stay stable:
renaming one is a breaking change and follows
[ADR 29](0029-no-api-versioning-until-go-live.md).

### One context for all document kinds

We use one JSON-LD context file for all document kinds. The API serves it
from its own endpoint. Responses only contain a link to it in `@context`, and
clients load the context with a separate request.

### Hydra for collections and pagination

We keep [Hydra](https://www.hydra-cg.com/spec/latest/core/) for search results
and other collections. Its terms (`member`, `totalItems`, `view`, `next`, …)
stay English, because they are technical terms of a standard vocabulary, not
domain terms.

### Approach

1. For each document kind, we check an example response for valid JSON-LD.
2. We build the JSON-LD context for all document kinds.
3. We migrate the endpoints of each document kind to German fields and the
   new context.

## Open questions

- Can the document kinds other than norms use `schema:CreativeWork`?
- Where do the descriptions of the `ris:` terms come from, and who maintains
  them?

## Consequences

- The API and the LDML documents mostly use the same German terms, but the
  `ris:` vocabulary is maintained separately from the glossary.

- Renaming fields to German changes the responses. How we version these
  changes is decided in [ADR 29](0029-no-api-versioning-until-go-live.md).

- Which data each endpoint returns, for example which metadata a search
  result contains, is not part of this ADR. See
  [ADR 28](0028-only-return-the-data-the-portal-needs.md).

- Every new field needs an entry in a context. A test that checks all fields
  map to an IRI makes sure we don't forget it.
