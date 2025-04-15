<script setup lang="ts">
import type {
  CaseLaw,
  LegislationWork,
  AnyDocument,
  SearchResult,
} from "@/types";
import CaselawSearchResult from "@/components/Search/Result/Caselaw/CaselawSearchResult.vue";
import NormSearchResult from "@/components/Search/Result/Norms/NormSearchResult.vue";

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
