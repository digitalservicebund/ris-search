# 27. HTML processing pipeline in the frontend

Date: 2026-11-17

## Status

Proposed

## Context

The backend sends pre-rendered HTML for the contents of documents such as legislation and case law. The frontend also shows HTML snippets in other places, mostly search results with highlighted matches. The backend sends HTML for performance and consistency reasons, or because the frontend does not have the necessary information.

The way we handle this HTML grew over time:

- Each place that uses HTML follows its own logic. They share hardly any code, and related code is spread across the codebase.
- We have no coherent strategy against manipulated HTML. We trust the backend in some places and sanitize in others, during both server-side and client-side rendering.
- We apply transforms such as link rewriting, extracting sections and hiding parts of the content in some places but not in others.
- We parse most documents twice, once on the server and again during hydration. We hide extracted parts with CSS instead of removing them from the content.

We are rebuilding this in [RISDEV-11107](https://digitalservicebund.atlassian.net/browse/RISDEV-11107). This ADR documents the approach.

## Decision

Whenever the frontend shows backend-provided HTML, it passes through the same stages:

1. Fetch the HTML from the backend.
2. Parse it into a DOM tree.
3. Apply transforms, such as link rewriting.
4. Extract partial content and remove it from the DOM.
5. Serialize what remains.
6. Sanitize the body and the extracted parts.
7. Return plain data: the body, the extracted parts as HTML, extracted text, and whether the document is empty.

Stages 2 to 7 live in one function that does not depend on Nuxt, so we can unit test it on its own. Each use case supplies its own transforms, extraction, allowlist and emptiness check.

Each use case has a composable that fetches the HTML and runs the pipeline inside `useAsyncData`. The result is plain, serializable data. The work happens either on the server or on the client, and the client does not parse again during hydration.

The pipeline removes extracted parts from the DOM, so the body does not contain them twice.

The pipeline is the only place where we sanitize backend-provided HTML. Components render pipeline output as is and do not sanitize it again.

Components show pipeline output through a single component instead of using `v-html` directly. This way, they cannot render HTML that has not passed through the pipeline.

Sanitizing needs an allowlist for each use case. The documents contain many elements and attributes the styling depends on, so we cannot guess them. Until we have these allowlists, the pipeline can skip sanitizing for a use case. We turn sanitizing on once we know the allowlist.

Short snippets, such as highlighted search results, need sanitizing but no DOM processing. They use a smaller variant of the pipeline with the same sanitizer.

## Consequences

- There is one place to look at and change when we handle backend-provided HTML. A new use case only supplies its own transforms, extraction and allowlist.

- Components no longer parse or sanitize backend-provided HTML, which removes duplicated work and gives us one place to defend against XSS.

- Until we turn on sanitizing for a use case, we trust its HTML as before. With the pipeline, turning it on is one visible switch instead of changes in many places.

- Allowlists need maintenance. When the backend adds elements or attributes that the styling needs, we must add them to the allowlist, or the styling breaks.
