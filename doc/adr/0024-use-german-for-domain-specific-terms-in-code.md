# 24. Use German for domain-specific terms in code

Date: 2026-10-05

## Status

Proposal

## Context

Many of the core concepts/domain terms of the legal information portal
have no precise English equivalent. Translating them leads to inconsistent
naming, multiple translations for the same concept, and terms that are
legally misleading (e.g. "case law" for _Rechtsprechung_). It also forces
developers to translate back and forth.

DigitalService has addressed this company-wide with the sensible default
[DR-0004: Language in Code](https://digitalservicebund.atlassian.net/wiki/spaces/DIGITALSER/pages/2062319701/DR-0004+Adopt+Sensible+Default+Language+in+Code).

## Decision

We adopt DR-0004 for this repository.

## Consequences

- Code uses the same vocabulary as domain experts, product and design,
  reducing translation overhead and misunderstandings.
- Names mix German and English ("Denglish"), which may be unfamiliar and
  harder to read for non-German speakers and external contributors.
- Existing English translations of domain terms are not renamed in bulk;
  they are migrated opportunistically when the code is touched.
