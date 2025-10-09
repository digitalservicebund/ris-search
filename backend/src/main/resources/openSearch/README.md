# OpenSearch Configuration Files

The OpenSearch configuration files in this directory define the analysis, filtering, and normalizing settings for the
indices used.
This document highlights specific choices made for the underlying data.

## Analysis Settings

The `analysis` section specifies custom analyzer definitions for text data in the index.

### Custom Analyzer Definitions

#### `custom_german_analyzer`

This analyzer is used for all text content in the project, such as headlines, document body, or metadata such as
location. The fields need to use the same analyzer in order to support our needed `CROSS_FIELDS` logic

## Normalizer Settings

The normalizer section is used for keyword fields.

### `normalized_keyword`
All keyword fields are currently indexed twice. Once as text (for the filtering logic using `CROSS_FIELDS`) and once
as keyword (with an exact match on keyword providing a large boost). Exact match means after normalization. The keyword
fields use `normalized_keyword` to apply `lowercase` and `asciifolding` so that "exact" match works as expected. In
particular "Abcü/123" will match "abcue/123", but will NOT match "Abcü 123".

## Document index definition

The document alias can be used to query all document kinds at once.
See [IndexAliasService.java](../../java/de/bund/digitalservice/ris/search/service/IndexAliasService.java) for reference.

## index definitions

The schema of each index is defined in it's template file. For example, norms is defined in [norms_index_template.json](norms_index_template.json).
They all reference the same analyzer which is defined in [german_analyzer_template.json](german_analyzer_template.json).
