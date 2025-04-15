# OpenSearch Configuration Files

The OpenSearch configuration files in this directory define the analysis, filtering, and normalizing settings for the
indices used.
This document highlights specific choices made for the underlying data.

## Analysis Settings

The `analysis` section specifies custom analyzer definitions for text data in the index.

### Custom Analyzer Definitions

#### `custom_german_analyzer`

This analyzer is used for most text content in the project, such as headlines, document body, or metadata such as
location.

#### `court_keyword_custom_german_analyzer`

This analyzer is similar to the `custom_german_analyzer`, but has support for domain-specific synonyms, such as "AG"
for "Amtsgericht".

## Normalizer Settings

The normalizer section is mainly used for keyword fields, doing character-level replacements:

### `file_number_normalizer`

This normalizer processes file numbers (Aktenzeichen) by filtering out special characters and spaces, and converting the
token to lowercase.
This way, both "IX ZR 100/10" and "ixzr10010" match the original input "IX ZR 100/10".

## Document index definition

The document alias can be used to query case law and norms at once.
See [IndexAliasService.java](../../java/de/bund/digitalservice/ris/search/service/IndexAliasService.java) for reference.

## CaseLaw index definiton

The [caselaw_mappings.json](./caselaw_mappings.json) document defines a list of aliases, so that both e.g. AZ,
AKTENZEICHEN, and file_numbers may be used in Lucene queries to refer to document_numbers.

### `_type` keywords fields

For court_type and document_type, a `keyword` field is defined so that exact matching based on court type may be
performed more efficiently.

### `file_numbers`

The file_numbers field is given a boost, so that users searching for a file number will see documents with that file
number first, before other documents that quote that number or have parts of that file number across different fields.

### `court_keyword`

See `court_keyword_custom_german_analyzer`.

### `articles`

This field is not actually used in the case law index. It is included to mirror the norms mapping, to enable queries that reference `articles` in the global `document` alias.
