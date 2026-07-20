<script setup lang="ts">
import { Message, PanelMenu, Select } from "primevue";
import type { MenuItem } from "primevue/menuitem";
import type { Statistics } from "~/types/api";
import { DocumentKind } from "~/types/api";
import { queryableDataFields } from "~/utils/search/dataFields";
import { isStrictDateFilterValue } from "~/utils/search/dateFilterType";
import { itemsPerPageOptions } from "~/utils/search/itemsPerPageOptions";

definePageMeta({
  alias: "/erweiterte-suche",
  middleware: () => {
    // For some reason our private feature flag composable doesn't work in this
    // context, falling back to the runtime config directly instead
    const config = useRuntimeConfig();
    if (!config.public.privateFeaturesEnabled) return abortNavigation();
  },
  skipLinks: [
    { label: "Zur Suche", to: "#search" },
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
  layout: false,
});

const route = useRoute();

const {
  dateFilter,
  documentKind,
  itemsPerPage,
  navigateToSearch,
  pageIndex,
  query,
  sort,
} = useAdvancedSearchRouteParams();

useSearchSeo({
  query,
  documentKind,
  pageIndex,
  searchType: "Erweiterte Suche",
  description:
    "Finden Sie gezielt Rechtsinformationen – schnell, präzise und übersichtlich.",
  ogTitle: "Erweiterte Suche im Rechtsinformationsportal des Bundes",
});

const searchFormId = useId();

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

const localDateFilter = ref(dateFilter.value);

watch(dateFilter, (val) => {
  localDateFilter.value = val;
});

const strictDateFilter = computed(() =>
  isStrictDateFilterValue(localDateFilter.value)
    ? localDateFilter.value
    : undefined,
);

// Query input ---------------------------------------------

const localQuery = ref(query.value);

watch(query, (val) => {
  localQuery.value = val;
});

// Document kind -------------------------------------------

const setDocumentKind: MenuItem["command"] = (e) => {
  if (!e.item.key) return;
  navigateToSearch({
    documentKind: e.item.key as DocumentKind,
    pageIndex: 0,
  });
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
    key: DocumentKind.AdministrativeDirective,
    label: formatDocumentKind(DocumentKind.AdministrativeDirective),
    command: setDocumentKind,
  },
  {
    key: DocumentKind.Literature,
    label: formatDocumentKind(DocumentKind.Literature),
    command: setDocumentKind,
  },
];

// Search results ------------------------------------------

const itemsPerPageLabelId = useId();
const resultsContainerRef = ref<HTMLElement | null>(null);
const scrollToResultsOnLoad = ref(false);

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

// Re-run search when URL changes
watch(
  () => route.query,
  async () => {
    await submitSearch();
  },
);

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
        await navigateToSearch({ pageIndex: lastPage }, { replace: true });
      }
    }
  },
  { immediate: true },
);

const formattedResultCount = computed(() =>
  formatResultCount(totalItemCount.value),
);

// User action handlers ------------------------------------

function submit() {
  navigateToSearch({ query: localQuery.value, pageIndex: 0 });
}

function handlePageUpdate(page: number) {
  scrollToResultsOnLoad.value = true;
  navigateToSearch({ pageIndex: page });
}

function updateSort(value: string | undefined) {
  navigateToSearch({
    sort: value ?? ADVANCED_SEARCH_DEFAULTS.sort,
    pageIndex: 0,
  });
}

function updateItemsPerPage(value: string | undefined) {
  navigateToSearch({
    itemsPerPage: value ?? ADVANCED_SEARCH_DEFAULTS.itemsPerPage,
    pageIndex: 0,
  });
}

function updateDateFilter(value: typeof dateFilter.value) {
  localDateFilter.value = value;
  if (!isStrictDateFilterValue(value)) return;
  navigateToSearch({ dateFilter: value, pageIndex: 0 });
}

watch(searchStatus, async (newStatus, oldStatus) => {
  const loadingSuccess = oldStatus === "pending" && newStatus === "success";
  if (loadingSuccess && scrollToResultsOnLoad.value) {
    scrollToResultsOnLoad.value = false;
    await nextTick();
    resultsContainerRef.value?.querySelector<HTMLAnchorElement>("a")?.focus();
  }
});
</script>

<template>
  <NuxtLayout name="breadcrumb-page">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Erweiterte Suche' }]" />
    </template>

    <div
      class="content-wrapper content-grid gap-y-40 pb-32 md:pb-56 lg:gap-y-64"
    >
      <div class="col-span-12">
        <h1 class="typo-headline1-bold mb-16">Erweiterte Suche</h1>
        <p class="text-balance">
          Nutzen Sie die erweiterte Suche, um genau das zu finden, was Sie
          brauchen – ob im Leitsatz, Titel oder direkt im Volltext. Mit
          Suchoperatoren wie AND, OR und NOT bekommen Sie noch präzisere
          Ergebnisse.
        </p>
      </div>

      <aside
        class="col-span-12 lg:col-span-4 lg:row-span-2 xl:col-span-3"
        aria-label="Filter"
      >
        <fieldset class="mb-40">
          <legend class="typo-label1-bold mb-8">Dokumentart</legend>
          <PanelMenu
            :model="documentKindMenuItems"
            :expanded-keys="{ [documentKind]: true }"
          />
        </fieldset>

        <SearchDateFilter
          v-model="localDateFilter"
          :document-kind
          @update:model-value="updateDateFilter"
        />
      </aside>

      <div id="search" class="col-span-12 lg:col-span-8 lg:col-start-5">
        <SearchDataFieldPicker
          v-model="localQuery"
          :data-fields="queryableDataFields"
          :document-kind
          :loading="searchStatus === 'pending'"
          :form-id="searchFormId"
          :count
          @submit="submit"
        />
      </div>

      <div
        ref="resultsContainerRef"
        id="search-results"
        class="col-span-12 grid scroll-mt-16 grid-cols-subgrid gap-y-32 lg:col-span-8 lg:col-start-5"
      >
        <div
          class="col-span-12 flex flex-col gap-16 md:flex-row md:items-center md:gap-48 lg:col-span-8"
        >
          <output
            aria-atomic="true"
            aria-live="polite"
            class="typo-label2-regular mr-auto text-nowrap"
          >
            {{ formattedResultCount }}
          </output>

          <div class="flex items-center gap-8">
            <label :id="itemsPerPageLabelId" class="typo-label2-regular">
              Einträge pro Seite
            </label>
            <Select
              :model-value="itemsPerPage"
              :aria-labelledby="itemsPerPageLabelId"
              :options="itemsPerPageOptions"
              @update:model-value="updateItemsPerPage"
            />
          </div>

          <SearchSortSelect
            :model-value="sort"
            :document-kind
            @update:model-value="updateSort"
          />
        </div>

        <div class="col-span-12 lg:col-span-7">
          <Pagination
            v-if="searchStatus !== 'idle'"
            :is-loading="searchStatus === 'pending'"
            :page="searchResults"
            navigation-position="bottom"
            @update-page="handlePageUpdate"
          >
            <Message v-if="!!searchError" severity="error">
              {{ searchError.message }}
            </Message>

            <ul
              v-if="searchResults"
              aria-label="Suchergebnisse"
              class="space-y-40"
            >
              <li
                v-for="(searchResult, order) in searchResults.member"
                :key="getIdentifier(searchResult.item)"
              >
                <SearchResult :search-result :order />
              </li>
            </ul>
          </Pagination>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>
