<script setup lang="ts">
import CaselawSearchResult from "~/components/Search/Result/Caselaw/CaselawSearchResult.vue";
import NormSearchResult from "~/components/Search/Result/Norms/NormSearchResult.vue";
import type {
  CaseLaw,
  LegislationWork,
  AnyDocument,
  SearchResult,
} from "~/types";

const props = defineProps<{
  searchResult: SearchResult<AnyDocument>;
  order: number;
}>();
</script>

<template>
  <CaselawSearchResult
    v-if="props.searchResult.item['@type'] === 'Decision'"
    :search-result="props.searchResult as SearchResult<CaseLaw>"
    :order="props.order"
  />
  <NormSearchResult
    v-else-if="props.searchResult.item['@type'] === 'Legislation'"
    :search-result="props.searchResult as SearchResult<LegislationWork>"
    :order="props.order"
  />
</template>
