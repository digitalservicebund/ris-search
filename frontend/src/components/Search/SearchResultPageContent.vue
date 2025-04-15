<script setup lang="ts">
import type { Page } from "@/components/Pagination/Pagination";
import SearchResult from "@/components/Search/Result/SearchResult.vue";
import { buildResultCountString } from "~/utils/paginationUtils";

const props = defineProps<{
  currentPage?: Page | undefined;
}>();
const searchResults = computed(() => props.currentPage?.member);
</script>

<template>
  <div v-if="currentPage" class="my-12 flex w-full">
    <span class="ris-label2-regular">
      {{ buildResultCountString(currentPage) }}
    </span>
  </div>
  <div v-if="searchResults && searchResults.length > 0" class="w-full">
    <SearchResult
      v-for="(element, index) in currentPage?.member"
      :key="index"
      :search-result="element"
      :order="index"
    />
  </div>
</template>
