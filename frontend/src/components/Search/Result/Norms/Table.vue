<script setup lang="ts">
import type { LegislationWork, SearchResult } from "@/types";
import dayjs from "dayjs";

defineProps<{
  searchResults: SearchResult<LegislationWork>[];
}>();
</script>

<template>
  <div class="ris-label2-bold sticky top-0 table-row bg-white text-gray-900">
    <div class="table-cell border-b-2 border-solid border-blue-300 px-16 py-12">
      Dokumentart
    </div>
    <div class="table-cell border-b-2 border-solid border-blue-300 px-16 py-12">
      Titel
    </div>
    <div class="table-cell border-b-2 border-solid border-blue-300 px-16 py-12">
      Veröffentlichungsdatum
    </div>
  </div>
  <div
    v-for="(searchResult, index) in searchResults"
    :key="index"
    class="ris-label1-regular table-row hover:bg-gray-100"
    data-testid="result-row"
  >
    <div class="table-cell border-b-1 border-blue-300 px-16 py-12 align-middle">
      N
    </div>
    <div class="table-cell border-b-1 border-blue-300 px-16 py-12 align-middle">
      <NuxtLink
        :to="`norms/${searchResult.item.workExample.legislationIdentifier}/regelungstext`"
      >
        {{ searchResult.item.name }}
      </NuxtLink>
    </div>
    <div class="table-cell border-b-1 border-blue-300 px-16 py-12 align-middle">
      {{
        searchResult.item.legislationDate
          ? dayjs(searchResult.item.legislationDate).format("DD.MM.YYYY")
          : "-"
      }}
    </div>
  </div>
</template>
