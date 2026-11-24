# 28. Only return the data the portal needs

Date: 2026-11-24

## Status

Proposed

## Context

There are no rules for which data an endpoint returns. Over time, the search
results and the single document responses have collected many metadata
fields, including some that neither the portal nor, as far as we know, API
users need.

Large responses take longer to load, both for the portal and for API users.
This matters most for search results, which contain many documents at once.

Every field in a response has to be mapped in the JSON-LD context (see
[ADR 27](0027-json-ld-with-german-fields-and-schema-org.md)), tested and
maintained. The full data of every document is also available as LDML from
the `.xml` endpoints.

## Decision

### Search results

A search result only contains metadata that is used in a filter or that the
portal shows in the search results.

### Single document endpoints

A single document response only contains the fields that the portal displays
on the document page.

The JSON-LD fields (`@context`, `@id`, `@type`) are always included. The
document text (e.g. Tatbestand, Gründe) comes from the HTML, not from the
JSON response. Highlights in search results come from `textMatches`, not from
the fields of the result.

### Fields per document kind

The fields each endpoint returns are listed in
[response-fields.md](../readme/response-fields.md). The list changes when the
portal needs another field or when the German names are clear, without
changing this ADR.

## Consequences

- Responses are smaller and load faster.

- Fewer fields in responses mean fewer fields to map, test and maintain.

- API users who need more data use the LDML from the `.xml` endpoints.

- A new field is only added to a response when the portal needs it.

- Removing fields changes the responses. How we version these changes is
  decided in [ADR 29](0029-no-api-versioning-until-go-live.md).
