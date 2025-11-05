<script setup lang="ts">
import CaselawSearchResult from "~/components/Search/CaselawSearchResult.vue";
import LiteratureSearchResult from "~/components/Search/LiteratureSearchResult.vue";
import NormSearchResult from "~/components/Search/NormSearchResult.vue";
import type { AnyDocument, SearchResult } from "~/types";
import {
  isCaselaw,
  isLegislationWork,
  isLiterature,
} from "~/utils/anyDocument";

const props = defineProps<{
  searchResult: SearchResult<AnyDocument>;
  order: number;
}>();
</script>

<template>
  <!-- @vue-expect-error -->
  <CaselawSearchResult
    v-if="isCaselaw(props.searchResult.item)"
    :search-result="props.searchResult"
    :order="props.order"
  />

  <NormSearchResult
    v-else-if="isLegislationWork(props.searchResult.item)"
    :search-result="props.searchResult"
    :order="props.order"
  />

  <LiteratureSearchResult
    v-else-if="isLiterature(props.searchResult.item)"
    :search-result="props.searchResult"
    :order="props.order"
  />
</template>
