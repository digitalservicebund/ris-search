<script setup lang="ts">
import AdministrativeDirectiveSearchResult from "~/components/Search/AdministrativeDirectiveSearchResult.vue";
import CaselawSearchResult from "~/components/Search/CaselawSearchResult.vue";
import LiteratureSearchResult from "~/components/Search/LiteratureSearchResult.vue";
import NormSearchResult from "~/components/Search/NormSearchResult.vue";
import type {
  AdministrativeDirective,
  AnyDocument,
  CaseLaw,
  LegislationWork,
  Literature,
  SearchResult,
} from "~/types";
import {
  isCaselaw,
  isLegislationWork,
  isLiterature,
  isAdministrativeDirective,
} from "~/utils/anyDocument";

const props = defineProps<{
  searchResult: SearchResult<AnyDocument>;
  order: number;
}>();
</script>

<template>
  <CaselawSearchResult
    v-if="isCaselaw(props.searchResult.item)"
    :search-result="props.searchResult as SearchResult<CaseLaw>"
    :order="props.order"
  />

  <NormSearchResult
    v-else-if="isLegislationWork(props.searchResult.item)"
    :search-result="props.searchResult as SearchResult<LegislationWork>"
    :order="props.order"
  />

  <LiteratureSearchResult
    v-else-if="isLiterature(props.searchResult.item)"
    :search-result="props.searchResult as SearchResult<Literature>"
    :order="props.order"
  />

  <AdministrativeDirectiveSearchResult
    v-else-if="isAdministrativeDirective(props.searchResult.item)"
    :search-result="props.searchResult as SearchResult<AdministrativeDirective>"
    :order="props.order"
  />
</template>
