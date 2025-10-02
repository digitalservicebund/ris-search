<script setup lang="ts">
import { Message, PanelMenu, Select } from "primevue";
import type { MenuItem } from "primevue/menuitem";
import type { LocationQueryValue } from "vue-router";
import { useRoute } from "#app";
import DataFieldPicker from "~/components/AdvancedSearch/DataFieldPicker.vue";
import DateFilter from "~/components/AdvancedSearch/DateFilter.vue";
import {
  isFilterType,
  type DateFilterValue,
} from "~/components/AdvancedSearch/filterType";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import Pagination from "~/components/Pagination/Pagination.vue";
import SortSelect from "~/components/Search/SortSelect.vue";
import { useAdvancedSearch } from "~/composables/useAdvancedSearch";
import { queryableDataFields } from "~/pages/advanced-search/dataFields";
import { DocumentKind } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";
import { isDocumentKind } from "~/utils/documentKind";
import { formatNumberWithSeparators } from "~/utils/numberFormatting";

const route = useRoute();

useHead({ title: "Erweiterte Suche" });
definePageMeta({ alias: "/erweiterte-suche" });

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

const documentKind = ref<DocumentKind>(
  typeof route.query.documentKind === "string" &&
    isDocumentKind(route.query.documentKind)
    ? route.query.documentKind
    : DocumentKind.Norm,
);

// Date filter --------------------------------------------

const dateFilter = ref<DateFilterValue>({
  type:
    typeof route.query.dateFilterType === "string" &&
    isFilterType(route.query.dateFilterType)
      ? route.query.dateFilterType
      : "allTime",
  from: route.query.dateFilterFrom?.toString(),
  to: route.query.dateFilterTo?.toString(),
});

const query = ref(route.query.q?.toString() ?? "");

function saveFilterStateToRoute() {
  navigateTo({
    query: {
      ...route.query,
      q: query.value,
      documentKind: documentKind.value,
      dateFilterType: dateFilter.value.type,
      dateFilterFrom: dateFilter.value.from ?? "",
      dateFilterTo: dateFilter.value.to ?? "",
      pageIndex: pageIndex.value,
      sort: sort.value,
      itemsPerPage: itemsPerPage.value,
    },
  });
}

// Sorting and pagination ---------------------------------

function tryGetPageIndexFromQuery(
  query: LocationQueryValue | LocationQueryValue[],
) {
  let result = 0;

  if (query) {
    const parsedNumber = Number.parseInt(query.toString());
    if (Number.isFinite(parsedNumber)) result = parsedNumber;
  }

  return result;
}

const sort = ref(route.query.sort?.toString() ?? "default");

const itemsPerPageDropdownId = useId();

const itemsPerPage = ref(route.query.itemsPerPage?.toString() ?? "50");

const itemsPerPageOptions = ["10", "50", "100"];

const pageIndex = ref(tryGetPageIndexFromQuery(route.query.pageIndex));

// Search results -----------------------------------------

const {
  searchError,
  searchResults,
  searchStatus,
  submitSearch,
  totalItemCount,
} = await useAdvancedSearch(query, documentKind, dateFilter, {
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
  () => [documentKind.value, sort.value, itemsPerPage.value, pageIndex.value],
  () => submit(),
);

function submit() {
  saveFilterStateToRoute();
  submitSearch();
}
</script>

<template>
  <ContentWrapper class="pb-32 lg:pb-64">
    <RisBreadcrumb title="Erweiterte Suche" base-path="/" />

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

      <div class="row-start-3 lg:row-start-auto">
        <div class="mb-40">
          <div class="ris-label1-regular mb-8">Dokumentart</div>
          <PanelMenu
            :model="documentKindMenuItems"
            :expanded-keys="{ [documentKind]: true }"
            :pt="{
              headercontent: { class: 'group' },
              headerlink: { class: 'no-underline group-hover:underline' },
              itemlink: { class: 'no-underline group-hover:underline' },
            }"
          />
        </div>

        <DateFilter v-model="dateFilter" :document-kind />
      </div>

      <div class="row-start-2 lg:row-start-auto">
        <DataFieldPicker
          v-model="query"
          :data-fields="queryableDataFields"
          :document-kind
          :loading="searchStatus === 'pending'"
          @submit="submit"
        />

        <Pagination
          :is-loading="searchStatus === 'pending'"
          :page="searchResults"
          navigation-position="bottom"
          @update-page="pageIndex = $event"
        >
          <div class="my-32 flex items-center gap-48">
            <span class="ris-subhead-regular mr-auto">
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
              :key="searchResult.item['@id']"
              :search-result
              :order="1"
            />
          </output>
        </Pagination>
      </div>
    </div>
  </ContentWrapper>
</template>
