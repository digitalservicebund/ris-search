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

All endpoint paths and response fields use German names. Field names follow
the type names in the XSD schema (e.g. `tatbestand` for `ris:tatbestand`), so
that the API, the schema and the glossary use the same terms.

### schema.org where possible

We map fields to schema.org where a suitable type or property exists:

- Norms map to [`schema:Legislation`](https://schema.org/Legislation).
- For the other document kinds, we still check whether
  [`schema:CreativeWork`](https://schema.org/CreativeWork) fits. If it doesn't,
  these document kinds use their own types with the `ris:` prefix.

Fields without a suitable schema.org property use the `ris:` prefix.

### `ris:` terms are the types of the XSD schema

Terms with the `ris:` prefix are the types of the XSD schema. Their
descriptions are the `xs:documentation` of those types, as listed in the
glossary that we generate from the XSD schema.

Until the API is official, the `ris:` IRIs don't resolve. Publishing the
definitions at those IRIs is a follow-up.

### One context for all document kinds

We use one JSON-LD context file for all document kinds. The API serves it
from its own endpoint. Responses only contain a link to it in `@context`, and
clients load the context with a separate request.

### Hydra for collections and pagination

We keep [Hydra](https://www.hydra-cg.com/spec/latest/core/) for search results
and other collections. Its terms (`member`, `totalItems`, `view`, `next`, …)
stay English, because they are technical terms of a standard vocabulary, not
domain terms.

### Search results contain only the metadata we need

A search result only contains metadata that is used in a filter or that the
frontend already shows. The full metadata stays in the detail response.

### Approach

1. For each document kind, we check an example response for valid JSON-LD.
2. We build the JSON-LD context for all document kinds.
3. We migrate the endpoints of each document kind to German fields and the
   new context.

## Open questions

- Can the document kinds other than norms use `schema:CreativeWork`?
- Which IRI does the `ris:` prefix expand to: the namespace of the XSD schema
  or an address of our own (today `https://rechtsinformationen.bund.de/vocab#`)?

## Consequences

- The API, the XSD schema and the glossary use the same German terms.

- Renaming fields and removing fields from search results change the
  responses. The API is not official yet, so we change the context and the
  endpoints directly, without versioning.

- Fewer fields in search results mean fewer fields to map, test and maintain.

- Every new field needs an entry in a context. A test that checks all fields
  map to an IRI makes sure we don't forget it.
