# 22. Use NormTestDataBuilder for norm LDML test data

Date: 2026-07-13

## Status

Draft

## Context

Norm LDML test data was previously created using raw static XML fixture
files and/or Pebble templates. This approach had several drawbacks:

- Tedious to maintain, especially when the LDML (LegalDocML.de) schema
  version changed, since every fixture or template had to be updated
  manually.
- Hard to grasp what data a given test actually relies on, as fixtures
  and templates mix the relevant test content with a lot of structural
  XML boilerplate.
- Fixtures and templates were cluttered with `GUID` and `eId` attributes
  that are largely irrelevant for most tests but required for valid,
  parsable LDML documents.
- Testing narrow behavior (e.g. extraction of a single element) required
  either large, complex templates with many conditional blocks, or a
  growing number of small raw XML files, both costly to maintain.

## Decision

We will use the `NormTestDataBuilder` as the standard
way to construct norm LDML test data, replacing raw XML fixtures and
template-based generation.

The builder generates valid LDML XML programmatically and takes care of
boilerplate such as GUIDs and eIds automatically, so tests only need to
specify the values relevant to what they actually verify. It also validates
the produced XMLs against the xsd files to make sure the test data is 
schema conform.

This allows a fluent and readable creation of test data that is schema conform
and easier to adapt in case of e.g. LDML version changes.

## Consequences

- Norm test data creation is centralized in the builder, so LDML/schema
  version changes only need to be incorporated in one place instead of
  across many fixtures and templates.
- Tests become easier to read, since the builder API directly expresses
  the relevant test data without unrelated XML boilerplate.
- Writing tests for narrow, isolated behavior (e.g. single element
  extraction) becomes straightforward, as the builder mostly provides sensible
  defaults for everything not explicitly set.
