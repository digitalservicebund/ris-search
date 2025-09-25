# OpenSearch Configuration Files

The OpenSearch configuration files in this directory define the analysis, filtering, and normalizing settings for the
indices used.
This document highlights specific choices made for the underlying data.

## Analysis Settings

The `analysis` section specifies custom analyzer definitions for text data in the index.

### Custom Analyzer Definitions

#### `custom_german_analyzer`

This analyzer is used for all text content in the project, such as headlines, document body, or metadata such as
location.

## Normalizer Settings

The normalizer section is used for keyword fields.

### `normalized_keyword`
All keyword fields are currently indexed twice. Once as text (for the filtering logic) and once
as keyword (with an exact match on keyword providing a large boost). Exact match means after normalization. The keyword
fields use `normalized_keyword` to apply `lowercase` and `asciifolding` so that "exact" match works as expected. In
particular "Abcü/123" will match "abcue/123", but will NOT match "Abcü 123".

## Document index definition

The document alias can be used to query case law and norms at once.
See [IndexAliasService.java](../../java/de/bund/digitalservice/ris/search/service/IndexAliasService.java) for reference.

## CaseLaw index definiton

The [caselaw_mappings.json](./caselaw_mappings.json) document defines a list of aliases, so that both e.g. AZ,
AKTENZEICHEN, and file_numbers may be used in Lucene queries to refer to document_numbers.

### `articles`

This field is not actually used in the case law index. It is included to mirror the norms mapping, to enable queries that reference `articles` in the global `document` alias.
