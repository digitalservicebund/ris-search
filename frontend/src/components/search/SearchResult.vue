<script setup lang="ts">
import type {
  AdministrativeDirective,
  AnyDocument,
  CaseLaw,
  LegislationExpression,
  Literature,
  SearchResult,
} from "~/types/api";

defineProps<{
  searchResult: SearchResult<AnyDocument>;
  order: number;
}>();
</script>

<template>
  <SearchCaselawSearchResult
    v-if="isCaselaw(searchResult.item)"
    :search-result="searchResult as SearchResult<CaseLaw>"
    :order="order"
  />

  <SearchNormSearchResult
    v-else-if="isLegislation(searchResult.item)"
    :search-result="searchResult as SearchResult<LegislationExpression>"
    :order="order"
  />

  <SearchLiteratureSearchResult
    v-else-if="isLiterature(searchResult.item)"
    :search-result="searchResult as SearchResult<Literature>"
    :order="order"
  />

  <SearchAdministrativeDirectiveSearchResult
    v-else-if="isAdministrativeDirective(searchResult.item)"
    :search-result="searchResult as SearchResult<AdministrativeDirective>"
    :order="order"
  />
</template>
