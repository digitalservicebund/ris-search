<script setup lang="ts">
import type { Page } from "@/components/Pagination/Pagination";
import { type DropdownItem, sortMode } from "@/components/types";
import * as searchService from "@/services/searchService";
import Button from "primevue/button";
import InputText from "primevue/inputtext";

import { computed } from "vue";

import { DocumentKind } from "@/types";
import SearchResult from "@/components/Search/Result/SearchResult.vue";
import SortSelect from "@/components/Search/SortSelect.vue";
import { buildResultCountString } from "~/utils/paginationUtils";

const userInputDisabled = ref(true);
onNuxtReady(() => {
  userInputDisabled.value = false;
});

const searchInput = ref("");
const isLoading = ref(false);
const hasError = ref(false);
const message = ref("");
const currentPage = ref<Page | undefined>();
const searchResults = computed(() => currentPage.value?.member);
const itemsPerPage = 100;
const pageNumber = ref<number>(0);

const currentSorting = ref(sortMode.default);
const currentSearchMode = ref("default");
const currentDocumentKind = ref<DocumentKind>(DocumentKind.All);
const textEntered = ref(false);
const modeChangeConfirmed = ref(false);
const builderUsed = ref(false);

const documentKind: ComputedRef<DocumentKind> = computed(() => {
  if (currentDocumentKind.value.length === 1) {
    return currentDocumentKind.value[0] as DocumentKind;
  }
  return DocumentKind.All;
});

const searchModeItems: DropdownItem[] = [
  { label: "Baukasten", value: "default" },
  { label: "Text", value: "text" },
];

const DocumentKinds: DropdownItem[] = [
  { label: "Alle Dokumentarten", value: "A" },
  { label: "Rechtsprechung", value: "R" },
  { label: "Normen", value: "N" },
];

watch(currentSorting, () => {
  if (searchInput.value) {
    search();
  }
});

watch(currentDocumentKind, () => {
  searchInput.value = "";
  currentPage.value = undefined;
});

async function handleSearchSubmit() {
  if (!searchInput.value) {
    message.value = "Bitte geben Sie eine Suchanfrage ein";
    hasError.value = true;
    return;
  }

  isLoading.value = true;
  message.value = "Lade…";
  hasError.value = false;
  currentPage.value = undefined;
  pageNumber.value = 0;

  await search();
}

function handleReset() {
  searchInput.value = "";
  currentPage.value = undefined;
  pageNumber.value = 0;
  message.value = "";
  hasError.value = false;
}

async function search() {
  try {
    const response = await searchService.advancedSearch({
      query: searchInput.value,
      itemsPerPage,
      pageNumber: pageNumber.value,
      sort: currentSorting.value,
      documentKind: currentDocumentKind.value,
    });
    currentPage.value = response.data;
    message.value = "";
  } catch (error) {
    message.value = (error as Error).message;
    hasError.value = true;
    showError(error as Error);
  } finally {
    isLoading.value = false;
  }
}

function disableAfterConfirmation() {
  if (!modeChangeConfirmed.value && builderUsed.value) {
    modeChangeConfirmed.value = confirm(
      "Nach der manuellen Eingabe des Suchtextes kann nicht mehr in den Baukasten-Modus zurückgewechselt werden. Möchten Sie fortfahren?",
    );
  }
  if (modeChangeConfirmed.value || !builderUsed.value) {
    textEntered.value = true;
  }
}

async function updatePage(page: number) {
  pageNumber.value = page;
  await search();
  setTimeout(() => {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }, 100);
}

function updateQuery(query?: string) {
  builderUsed.value = true;
  searchInput.value = query || "";
}
</script>

<template>
  <div class="flex flex-col gap-8">
    <div class="relative flex flex-wrap space-x-4">
      <DropdownInput
        id="documentKind"
        v-model="currentDocumentKind"
        aria-label="Dokumentart"
        class="ds-select-small w-auto"
        :items="DocumentKinds"
        :disabled="userInputDisabled"
      />
      <DropdownInput
        id="searchMode"
        v-model="currentSearchMode"
        aria-label="Sucheingabemodus"
        class="ds-select-small w-auto"
        :items="searchModeItems"
        :disabled="textEntered || userInputDisabled"
      />
    </div>

    <SearchQueryBuilder
      v-show="currentSearchMode === 'default' && !userInputDisabled"
      :current-document-kind="currentDocumentKind"
      @update:lucene-query="updateQuery"
    />

    <div class="flex flex-col gap-4">
      <div v-if="currentSearchMode === 'text'">
        <InputText
          id="searchInput"
          v-model="searchInput"
          aria-label="Suchanfrage"
          fluid
          :disabled="isLoading"
          placeholder="Suchanfrage"
          @enter-released="handleSearchSubmit"
          @input="disableAfterConfirmation"
        />
      </div>

      <div class="flex flex-row items-center gap-8">
        <Button
          v-if="searchInput !== ''"
          aria-label="Suchen"
          class="self-start"
          :disabled="isLoading || userInputDisabled"
          label="Suchen"
          @click="handleSearchSubmit"
        />
        <Button
          v-if="currentSearchMode === 'text' && searchInput !== ''"
          aria-label="Zurücksetzen"
          label="Zurücksetzen"
          @click="handleReset"
        />
      </div>
      <p :class="{ 'text-red-700': hasError }">{{ message }}</p>
    </div>
    <DelayedLoadingMessage v-if="userInputDisabled"
      >Lade Suche...</DelayedLoadingMessage
    >
    <div
      v-if="searchResults && searchResults.length > 0 && searchInput !== ''"
      class="flex items-center justify-end gap-8"
    >
      <div class="w-auto">
        <SortSelect v-model="currentSorting" :document-kind="documentKind" />
      </div>
    </div>
    <Pagination
      v-if="searchInput !== ''"
      :is-loading="isLoading"
      navigation-position="bottom"
      :page="currentPage"
      @update-page="updatePage"
    >
      <div v-if="currentPage" class="flex w-full justify-center">
        <output aria-live="polite" aria-atomic="true" class="ris-label2-bold">
          {{ currentPage ? buildResultCountString(currentPage) : "" }}
        </output>
      </div>
      <div
        v-if="searchResults && searchResults.length > 0"
        class="relative mt-8 table w-full border-separate"
      >
        <SearchResult
          v-for="(element, index) in searchResults"
          :key="index"
          :search-result="element"
          :order="index"
        />
      </div>
    </Pagination>
  </div>
</template>
