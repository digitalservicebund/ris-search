<script setup lang="ts">
import { Message, PanelMenu, Select } from "primevue";
import type { MenuItem } from "primevue/menuitem";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import Pagination from "~/components/Pagination/Pagination.vue";
import DataFieldPicker from "~/components/Search/DataFieldPicker.vue";
import DateFilter from "~/components/Search/DateFilter.vue";
import SortSelect from "~/components/Search/SortSelect.vue";
import { useAdvancedSearch } from "~/composables/useAdvancedSearch";
import { useAdvancedSearchRouteParams } from "~/composables/useAdvancedSearchRouteParams";
import { DocumentKind } from "~/types";
import { getIdentifier } from "~/utils/anyDocument";
import { formatDocumentKind } from "~/utils/displayValues";
import { formatNumberWithSeparators } from "~/utils/numberFormatting";
import { queryableDataFields } from "~/utils/search/dataFields";
import { isStrictDateFilterValue } from "~/utils/search/filterType";

useHead({ title: "Erweiterte Suche" });
definePageMeta({ alias: "/erweiterte-suche" });

const {
  dateFilter,
  documentKind,
  itemsPerPage,
  pageIndex,
  query,
  saveFilterStateToRoute,
  sort,
} = useAdvancedSearchRouteParams();

const searchFormId = useId();

// Date filter ---------------------------------------------

const strictDateFilter = ref(
  isStrictDateFilterValue(dateFilter.value) ? dateFilter.value : undefined,
);

watch(dateFilter, (newVal) => {
  if (isStrictDateFilterValue(newVal)) strictDateFilter.value = newVal;
});

// Document kind -------------------------------------------

const setDocumentKind: MenuItem["command"] = (e) => {
  if (!e.item.key) return;
  documentKind.value = e.item.key as DocumentKind;
};

const documentKindMenuItems: MenuItem[] = [
  {
    key: DocumentKind.Norm,
    label: formatDocumentKind(DocumentKind.Norm),
    command: setDocumentKind,
  },
  {
    key: DocumentKind.CaseLaw,
    label: formatDocumentKind(DocumentKind.CaseLaw),
    command: setDocumentKind,
  },
];

// Search results -----------------------------------------

const itemsPerPageDropdownId = useId();

const itemsPerPageOptions = ["10", "50", "100"];

const {
  searchError,
  searchResults,
  searchStatus,
  submitSearch,
  totalItemCount,
} = await useAdvancedSearch(query, documentKind, strictDateFilter, {
  itemsPerPage,
  sort,
  pageIndex,
});

// Perform initial search with any existing filter + query params
await submitSearch();

const formattedResultCount = computed(() =>
  formatNumberWithSeparators(totalItemCount.value),
);

// Auto reload for "discrete" actions
watch(
  () => [
    documentKind.value,
    sort.value,
    itemsPerPage.value,
    pageIndex.value,
    strictDateFilter.value,
  ],
  () => submit(),
);

async function submit() {
  await saveFilterStateToRoute();
  submitSearch();
}
</script>

<template>
  <ContentWrapper class="pb-32 lg:pb-64">
    <RisBreadcrumb :items="[{ label: 'Erweiterte Suche' }]" />
    <div
      class="mt-24 grid grid-cols-1 gap-40 lg:grid-cols-[330px_1fr] lg:gap-64"
    >
      <div class="lg:col-span-2">
        <h1 class="ris-heading2-bold mb-16">Erweiterte Suche</h1>
        <p class="ris-body1-regular text-balance">
          Nutzen Sie die erweiterte Suche, um genau das zu finden, was Sie
          brauchen – ob im Leitsatz, Titel oder direkt im Volltext. Mit
          Suchoperatoren wie AND, OR und NOT bekommen Sie noch präzisere
          Ergebnisse.
        </p>
      </div>

      <div class="row-start-3 lg:row-span-2 lg:row-start-auto">
        <fieldset class="mb-40">
          <legend class="ris-label1-regular mb-8">Dokumentart</legend>
          <PanelMenu
            :model="documentKindMenuItems"
            :expanded-keys="{ [documentKind]: true }"
          />
        </fieldset>

        <DateFilter v-model="dateFilter" :document-kind />
      </div>

      <div class="row-start-2 lg:row-start-auto">
        <DataFieldPicker
          v-model="query"
          :data-fields="queryableDataFields"
          :document-kind
          :loading="searchStatus === 'pending'"
          :form-id="searchFormId"
          @submit="submit"
        />
      </div>

      <div>
        <Pagination
          v-if="searchStatus !== 'idle'"
          :is-loading="searchStatus === 'pending'"
          :page="searchResults"
          navigation-position="bottom"
          @update-page="pageIndex = $event"
        >
          <div
            class="mb-32 flex flex-col gap-16 md:flex-row md:items-center md:gap-48"
          >
            <span class="ris-subhead-regular mr-auto text-nowrap">
              {{ formattedResultCount }} Suchergebnisse
            </span>
            <SortSelect v-model="sort" :document-kind />

            <label
              :for="itemsPerPageDropdownId"
              class="ris-label2-regular flex items-center gap-8"
            >
              Einträge pro Seite
              <Select
                :id="itemsPerPageDropdownId"
                v-model="itemsPerPage"
                :options="itemsPerPageOptions"
              />
            </label>
          </div>

          <Message v-if="!!searchError" severity="error" class="max-w-prose">
            {{ searchError.message }}
          </Message>

          <output v-if="searchResults">
            <SearchResult
              v-for="searchResult in searchResults.member"
              :key="getIdentifier(searchResult.item)"
              :search-result
              :order="1"
            />
          </output>
        </Pagination>
      </div>
    </div>
  </ContentWrapper>
</template>
