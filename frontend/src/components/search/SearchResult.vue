<script setup lang="ts">
import type {
  AdministrativeDirectiveSearchSchema,
  AnyDocument,
  CaseLawSearchSchema,
  LegislationExpression,
  LiteratureSearchSchema,
  SearchResult,
} from "~/types/api";
import type { SearchResultHeadingLevel } from "~/utils/search/searchResults";

const { headingLevel = "2" } = defineProps<{
  searchResult: SearchResult<AnyDocument>;
  headingLevel?: SearchResultHeadingLevel;
}>();
</script>

<template>
  <SearchCaselawSearchResult
    v-if="isCaselaw(searchResult.item)"
    :search-result="searchResult as SearchResult<CaseLawSearchSchema>"
    :heading-level="headingLevel"
  />

  <SearchNormSearchResult
    v-else-if="isLegislation(searchResult.item)"
    :search-result="searchResult as SearchResult<LegislationExpression>"
    :heading-level="headingLevel"
  />

  <SearchLiteratureSearchResult
    v-else-if="isLiterature(searchResult.item)"
    :search-result="searchResult as SearchResult<LiteratureSearchSchema>"
    :heading-level="headingLevel"
  />

  <SearchAdministrativeDirectiveSearchResult
    v-else-if="isAdministrativeDirective(searchResult.item)"
    :search-result="
      searchResult as SearchResult<AdministrativeDirectiveSearchSchema>
    "
    :heading-level="headingLevel"
  />
</template>
