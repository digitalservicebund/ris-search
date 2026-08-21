<script setup lang="ts">
import IcBaselineSwapVert from "~icons/ic/baseline-swap-vert";
import IcOutlineFilterAlt from "~icons/ic/outline-filter-alt";
import type { RadioTreeItem } from "~/components/ui/RadioTree.vue";
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

const setDocumentKind = (value: string) => {
  navigateToSearch({
    documentKind: value as DocumentKind,
    pageIndex: 0,
  });
};

const documentKindItems: RadioTreeItem[] = [
  DocumentKind.Norm,
  DocumentKind.CaseLaw,
  DocumentKind.AdministrativeDirective,
  DocumentKind.Literature,
].map((kind) => ({ value: kind, label: formatDocumentKind(kind) }));

// Mobile filter/sort drawers -------------------------------

const filterDrawerVisible = ref(false);
const sortDrawerVisible = ref(false);

const dateFilterDraft = useDraftValue(dateFilter, filterDrawerVisible, {
  type: getDefaultDateFilterType(documentKind.value),
});

const sortDraft = useDraftValue(
  sort,
  sortDrawerVisible,
  ADVANCED_SEARCH_DEFAULTS.sort,
);

const itemsPerPageDraft = useDraftValue(
  itemsPerPage,
  sortDrawerVisible,
  ADVANCED_SEARCH_DEFAULTS.itemsPerPage,
);

function applyFilterDrawer() {
  const strictDraftDateFilter = isStrictDateFilterValue(
    dateFilterDraft.draft.value,
  )
    ? dateFilterDraft.draft.value
    : undefined;

  navigateToSearch({
    ...(strictDraftDateFilter ? { dateFilter: strictDraftDateFilter } : {}),
    pageIndex: 0,
  });
}

function resetFilterDrawer() {
  dateFilterDraft.draft.value = {
    type: getDefaultDateFilterType(documentKind.value),
  };
  applyFilterDrawer();
}

function applySortDrawer() {
  navigateToSearch({
    sort: sortDraft.draft.value ?? ADVANCED_SEARCH_DEFAULTS.sort,
    itemsPerPage:
      itemsPerPageDraft.draft.value ?? ADVANCED_SEARCH_DEFAULTS.itemsPerPage,
    pageIndex: 0,
  });
}

function resetSortDrawer() {
  sortDraft.reset();
  itemsPerPageDraft.reset();
  applySortDrawer();
}

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

const { isSearching } = useSearchLoadingIndicator(searchStatus);

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
        <fieldset class="md:mb-40">
          <legend class="typo-label1-bold mb-8">Dokumentart</legend>
          <UiRadioTree
            :items="documentKindItems"
            :model-value="documentKind"
            @update:model-value="setDocumentKind"
          />
        </fieldset>

        <div class="hidden md:block">
          <SearchDateFilter
            v-model="localDateFilter"
            :document-kind
            @update:model-value="updateDateFilter"
          />
        </div>
      </aside>

      <div
        id="search"
        class="col-span-12 row-2 md:row-auto lg:col-span-8 lg:col-start-5"
      >
        <SearchDataFieldPicker
          v-model="localQuery"
          :data-fields="queryableDataFields"
          :document-kind
          :loading="isSearching"
          :form-id="searchFormId"
          :count
          @submit="submit"
        />
      </div>

      <div
        ref="resultsContainerRef"
        id="search-results"
        class="col-span-12 grid scroll-mt-16 grid-cols-subgrid gap-y-24 md:gap-y-32 lg:col-span-8 lg:col-start-5"
      >
        <div
          class="col-span-12 flex flex-col gap-24 md:flex-row md:items-center md:gap-48 lg:col-span-8"
        >
          <div class="flex gap-8 md:hidden">
            <SearchMobileActionDrawer
              v-model:visible="filterDrawerVisible"
              class="flex-1"
              label="Filtern"
              :icon="IcOutlineFilterAlt"
              @reset="resetFilterDrawer"
              @apply="applyFilterDrawer"
            >
              <SearchDateFilter
                v-model="dateFilterDraft.draft.value"
                :document-kind
              />
            </SearchMobileActionDrawer>

            <SearchMobileActionDrawer
              v-model:visible="sortDrawerVisible"
              class="flex-1"
              label="Sortieren"
              :icon="IcBaselineSwapVert"
              @reset="resetSortDrawer"
              @apply="applySortDrawer"
            >
              <SearchSortOptionsRadioGroup
                v-model="sortDraft.draft.value"
                :document-kind
              />

              <SearchItemsPerPageRadioGroup
                v-model="itemsPerPageDraft.draft.value"
              />
            </SearchMobileActionDrawer>
          </div>

          <output
            aria-atomic="true"
            aria-live="polite"
            class="typo-label2-regular border-b border-b-gray-400 pb-16 text-nowrap md:mr-auto md:border-none md:pb-0"
          >
            {{ formattedResultCount }}
          </output>

          <div class="hidden items-center gap-8 md:flex">
            <label :id="itemsPerPageLabelId" class="typo-label2-regular">
              Einträge pro Seite
            </label>
            <UiSelect
              :model-value="itemsPerPage"
              :aria-labelledby="itemsPerPageLabelId"
              :options="itemsPerPageOptions"
              @update:model-value="updateItemsPerPage"
            />
          </div>

          <SearchSortSelect
            class="hidden md:flex"
            :model-value="sort"
            :document-kind
            @update:model-value="updateSort"
          />
        </div>

        <div class="col-span-12 lg:col-span-7">
          <Pagination
            :page="searchResults"
            navigation-position="bottom"
            @update-page="handlePageUpdate"
          >
            <UiMessage v-if="!!searchError" severity="error" role="alert">
              {{ searchError.message }}
            </UiMessage>

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
