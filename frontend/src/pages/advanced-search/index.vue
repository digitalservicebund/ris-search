<script setup lang="ts">
import { Message, PanelMenu, Select } from "primevue";
import type { MenuItem } from "primevue/menuitem";
import Pagination from "~/components/Pagination.vue";
import DataFieldPicker from "~/components/search/DataFieldPicker.vue";
import DateFilter from "~/components/search/DateFilter.vue";
import SortSelect from "~/components/search/SortSelect.vue";
import { useAdvancedSearch } from "~/composables/useAdvancedSearch";
import { useAdvancedSearchRouteParams } from "~/composables/useAdvancedSearchRouteParams";
import type { Statistics } from "~/types";
import { DocumentKind } from "~/types";
import { getIdentifier } from "~/utils/anyDocument";
import { formatDocumentKind } from "~/utils/displayValues";
import { formatNumberWithSeparators } from "~/utils/numberFormatting";
import { queryableDataFields } from "~/utils/search/dataFields";
import { isStrictDateFilterValue } from "~/utils/search/filterType";

useHead({ title: "Erweiterte Suche" });

definePageMeta({
  alias: "/erweiterte-suche",
  middleware: () => {
    // For some reason our private feature flag composable doesn't work in this
    // context, falling back to the runtime config directly instead
    const config = useRuntimeConfig();
    if (!config.public.privateFeaturesEnabled) return abortNavigation();
  },
});

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

watch(documentKind, () => {
  query.value = "";
});

// Stats ---------------------------------------------------

const { data: stats } = await useRisBackend<Statistics>("/v1/statistics");

const count = computed(() =>
  stats.value
    ? {
        [DocumentKind.CaseLaw]: stats.value["case-law"]?.count,
        [DocumentKind.Literature]: stats.value.literature?.count,
        [DocumentKind.Norm]: stats.value.legislation?.count,
        [DocumentKind.AdministrativeDirective]:
          stats.value["administrative-directive"]?.count,
      }
    : undefined,
);

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
  {
    key: DocumentKind.Literature,
    label: formatDocumentKind(DocumentKind.Literature),
    command: setDocumentKind,
  },
  {
    key: DocumentKind.AdministrativeDirective,
    label: formatDocumentKind(DocumentKind.AdministrativeDirective),
    command: setDocumentKind,
  },
];

// Search results ------------------------------------------

const itemsPerPageDropdownId = useId();
const resultsContainerRef = ref<HTMLElement | null>(null);
const scrollToResultsOnLoad = ref(false);

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

// Watch for changes in page size, so that the page number is adjusted accordingly
watch(
  () => searchResults.value,
  async (page) => {
    if (!page) return;

    const totalItems = page.totalItems ?? 0;
    const requestedPage = pageIndex.value;
    const perPage = Number(itemsPerPage.value);

    if (page.member.length === 0 && totalItems > 0) {
      const lastPage = Math.floor((totalItems - 1) / perPage);

      if (requestedPage !== lastPage) {
        pageIndex.value = lastPage;
        await saveFilterStateToRoute();
        await submitSearch();
      }
    }
  },
  { immediate: true },
);

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

function handlePageUpdate(page: number) {
  scrollToResultsOnLoad.value = true;
  pageIndex.value = page;
}

watch(searchStatus, async (newStatus, oldStatus) => {
  const loadingSuccess = oldStatus === "pending" && newStatus === "success";
  if (loadingSuccess && scrollToResultsOnLoad.value) {
    scrollToResultsOnLoad.value = false;
    await nextTick();
    resultsContainerRef.value?.scrollIntoView({ behavior: "smooth" });
  }
});
</script>

<template>
  <Breadcrumbs :items="[{ label: 'Erweiterte Suche' }]" />

  <div class="mt-24 grid grid-cols-1 gap-40 lg:grid-cols-[20rem_1fr] lg:gap-64">
    <div class="lg:col-span-2">
      <h1 class="ris-heading2-bold mb-16">Erweiterte Suche</h1>
      <p class="text-balance">
        Nutzen Sie die erweiterte Suche, um genau das zu finden, was Sie
        brauchen – ob im Leitsatz, Titel oder direkt im Volltext. Mit
        Suchoperatoren wie AND, OR und NOT bekommen Sie noch präzisere
        Ergebnisse.
      </p>
    </div>

    <aside
      class="row-start-3 lg:row-span-2 lg:row-start-auto"
      aria-label="Filter"
    >
      <fieldset class="mb-40">
        <legend class="ris-label1-regular mb-8">Dokumentart</legend>
        <PanelMenu
          :model="documentKindMenuItems"
          :expanded-keys="{ [documentKind]: true }"
        />
      </fieldset>

      <DateFilter v-model="dateFilter" :document-kind />
    </aside>

    <div class="row-start-2 lg:row-start-auto">
      <DataFieldPicker
        v-model="query"
        :data-fields="queryableDataFields"
        :document-kind
        :loading="searchStatus === 'pending'"
        :form-id="searchFormId"
        :count
        @submit="submit"
      />
    </div>

    <div ref="resultsContainerRef" class="scroll-mt-16">
      <Pagination
        v-if="searchStatus !== 'idle'"
        :is-loading="searchStatus === 'pending'"
        :page="searchResults"
        navigation-position="bottom"
        @update-page="handlePageUpdate"
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

        <div class="max-w-prose">
          <Message v-if="!!searchError" severity="error">
            {{ searchError.message }}
          </Message>

          <ul v-if="searchResults" aria-label="Suchergebnisse">
            <li
              v-for="(searchResult, order) in searchResults.member"
              :key="getIdentifier(searchResult.item)"
            >
              <SearchResult :search-result :order />
            </li>
          </ul>
        </div>
      </Pagination>
    </div>
  </div>
</template>
