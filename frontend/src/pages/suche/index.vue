<script setup lang="ts">
import { Message, Select } from "primevue";
import IcBaselineSwapVert from "~icons/ic/baseline-swap-vert";
import IcOutlineFilterAlt from "~icons/ic/outline-filter-alt";
import { DocumentKind } from "~/types/api";
import { isStrictDateFilterValue } from "~/utils/search/dateFilterType";
import { itemsPerPageOptions } from "~/utils/search/itemsPerPageOptions";

definePageMeta({
  skipLinks: [
    { label: "Zur Suche", to: "#search" },
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
  layout: false,
});

const filterHeadingId = useId();
const route = useRoute();

const {
  court,
  dateFilter,
  documentKind,
  itemsPerPage,
  navigateToSearch,
  pageIndex,
  query,
  sort,
  typeGroup,
} = useSimpleSearchRouteParams();

useSearchSeo({
  query,
  documentKind,
  pageIndex,
  searchType: "Suche",
  description:
    "Finden Sie gezielt Gesetze, Verordnungen und Entscheidungen – schnell, präzise und übersichtlich.",
  ogTitle: "Suche im Rechtsinformationsportal des Bundes",
});

const privateFeaturesEnabled = usePrivateFeaturesFlag();

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

// Document kind -------------------------------------------

const categoryFilterValue = computed(() => {
  let val = documentKind.value.toString();
  if (typeGroup.value) val += `.${typeGroup.value}`;
  return val;
});

function updateCategoryFilter(value: string) {
  const [maybeKind, group] = value.split(".");

  let kind = DocumentKind.All;
  if (maybeKind && isDocumentKind(maybeKind)) kind = maybeKind;

  navigateToSearch({ documentKind: kind, typeGroup: group, pageIndex: 0 });
}

const documentKindAndGroup = computed(() => ({
  documentKind: documentKind.value,
  typeGroup: typeGroup.value,
}));

const hasSubFilters = computed(
  () =>
    documentKind.value === DocumentKind.CaseLaw ||
    documentKind.value === DocumentKind.AdministrativeDirective ||
    documentKind.value === DocumentKind.Literature,
);

// Mobile filter/sort drawers -------------------------------

const filterDrawerVisible = ref(false);
const sortDrawerVisible = ref(false);

const courtDraft = useDraftValue(court, filterDrawerVisible, undefined);

const dateFilterDraft = useDraftValue(dateFilter, filterDrawerVisible, {
  type: SIMPLE_SEARCH_DEFAULTS.dateFilterType,
});

const sortDraft = useDraftValue(
  sort,
  sortDrawerVisible,
  SIMPLE_SEARCH_DEFAULTS.sort,
);

const itemsPerPageDraft = useDraftValue(
  itemsPerPage,
  sortDrawerVisible,
  SIMPLE_SEARCH_DEFAULTS.itemsPerPage,
);

function resetFilterDrawer() {
  courtDraft.reset();
  dateFilterDraft.reset();
  applyFilterDrawer();
}

function applyFilterDrawer() {
  const strictDraftDateFilter = isStrictDateFilterValue(
    dateFilterDraft.draft.value,
  )
    ? dateFilterDraft.draft.value
    : undefined;

  navigateToSearch({
    court: courtDraft.draft.value,
    ...(strictDraftDateFilter ? { dateFilter: strictDraftDateFilter } : {}),
    pageIndex: 0,
  });
}

function resetSortDrawer() {
  sortDraft.reset();
  itemsPerPageDraft.reset();
  applySortDrawer();
}

function applySortDrawer() {
  navigateToSearch({
    sort: sortDraft.draft.value ?? SIMPLE_SEARCH_DEFAULTS.sort,
    itemsPerPage:
      itemsPerPageDraft.draft.value ?? SIMPLE_SEARCH_DEFAULTS.itemsPerPage,
    pageIndex: 0,
  });
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
} = await useSimpleSearch(
  query,
  documentKindAndGroup,
  strictDateFilter,
  court,
  {
    itemsPerPage,
    sort,
    pageIndex,
  },
);

// Perform initial search with any existing filter + query params
await submitSearch();

watch(
  searchError,
  (val) => {
    if (val) showError(val);
  },
  { immediate: true },
);

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

const formattedResultCount = computed(() => {
  if (isLoading.value) return "";
  return formatResultCount(totalItemCount.value);
});

const isLoading = computed(() => searchStatus.value === "pending");

// User action handlers ------------------------------------

function updateQuery(value: string | undefined) {
  navigateToSearch({ query: value ?? "", pageIndex: 0 });
}

function handleEmptySearch() {
  navigateToSearch({ query: "", pageIndex: 0 });
}

async function updatePage(page: number) {
  scrollToResultsOnLoad.value = true;
  navigateToSearch({ pageIndex: page });
}

function updateSort(value: string | undefined) {
  navigateToSearch({
    sort: value ?? SIMPLE_SEARCH_DEFAULTS.sort,
    pageIndex: 0,
  });
}

function updateItemsPerPage(value: string | undefined) {
  navigateToSearch({
    itemsPerPage: value ?? SIMPLE_SEARCH_DEFAULTS.itemsPerPage,
    pageIndex: 0,
  });
}

function updateCourt(value: string | undefined) {
  navigateToSearch({ court: value, pageIndex: 0 });
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
      <Breadcrumbs :items="[{ label: 'Suche' }]" />
    </template>

    <div class="content-wrapper pb-32 md:pb-56">
      <h1 class="typo-headline1-bold inline-block pb-8">Suche</h1>

      <div id="search">
        <SearchSimpleSearchInput
          :model-value="query"
          @update:model-value="updateQuery"
          @empty-search="handleEmptySearch"
        />
      </div>

      <SkipLink to="#search-results" class="mt-8">
        Zu den Ergebnissen
      </SkipLink>

      <p v-if="privateFeaturesEnabled" class="typo-label2-regular mt-8">
        Mehr Suchoptionen finden Sie unter
        <NuxtLink
          :to="{ name: 'erweiterte-suche' }"
          class="ris-link2-bold 2xl:ris-link1-bold"
        >
          Erweiterte Suche
        </NuxtLink>
      </p>

      <div class="content-grid mt-32 gap-y-24 md:gap-y-32">
        <div
          class="col-span-12 row-start-2 flex flex-col gap-24 md:row-auto md:flex-row md:flex-wrap md:items-center md:justify-between md:gap-x-32"
        >
          <div class="flex gap-8 md:hidden">
            <SearchMobileActionDrawer
              v-if="hasSubFilters"
              v-model:visible="filterDrawerVisible"
              class="flex-1"
              label="Filtern"
              :icon="IcOutlineFilterAlt"
              @reset="resetFilterDrawer"
              @apply="applyFilterDrawer"
            >
              <SearchCourtFilter
                v-if="documentKind === DocumentKind.CaseLaw"
                v-model="courtDraft.draft.value"
                append-to="body"
              />

              <SearchDateFilter
                v-if="hasSubFilters"
                v-model="dateFilterDraft.draft.value"
                :document-kind="documentKind"
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
            class="typo-label2-regular border-b border-b-gray-400 pb-16 md:border-none md:pb-0"
          >
            {{ isLoading ? "Lade ..." : formattedResultCount }}
          </output>

          <div class="hidden flex-wrap gap-x-32 gap-y-16 md:flex">
            <div class="flex items-center gap-8">
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
              :model-value="sort"
              :document-kind
              @update:model-value="updateSort"
            />
          </div>
        </div>

        <aside
          class="col-span-12 md:col-span-4 md:pb-10 lg:col-span-3"
          :aria-labelledby="filterHeadingId"
        >
          <h2
            :id="filterHeadingId"
            class="typo-label1-bold mb-16 flex items-center"
          >
            Dokumentart
          </h2>

          <div class="flex flex-col gap-24">
            <SearchCategoryFilter
              :model-value="categoryFilterValue"
              @update:model-value="updateCategoryFilter"
            />

            <div class="hidden flex-col gap-24 md:flex">
              <SearchCourtFilter
                v-if="documentKind === DocumentKind.CaseLaw"
                :model-value="court"
                @update:model-value="updateCourt"
              />

              <SearchDateFilter
                v-if="hasSubFilters"
                v-model="localDateFilter"
                :document-kind="documentKind"
                @update:model-value="updateDateFilter"
              />
            </div>
          </div>
        </aside>

        <div
          id="search-results"
          ref="resultsContainerRef"
          class="col-span-12 scroll-mt-16 flex-col justify-end gap-8 md:col-span-8 lg:col-span-8 lg:col-start-5 xl:col-span-7 xl:col-start-5"
        >
          <Pagination
            :is-loading="isLoading"
            :page="searchResults"
            navigation-position="bottom"
            @update-page="updatePage"
          >
            <Message v-if="!!searchError" severity="error">
              {{ searchError.message }}
            </Message>

            <Message
              severity="warn"
              class="ris-body2-regular"
              role="status"
              aria-live="off"
            >
              <p class="ris-body2-bold mt-2">
                Dieser Service befindet sich in der Testphase.
              </p>
              <p>
                Der Datenbestand ist noch nicht vollständig und die
                Suchpriorisierung noch nicht final. Der Service ist in
                Entwicklung. Wir arbeiten an der Ergänzung und Darstellung aller
                Inhalte.
              </p>
            </Message>

            <ul v-if="searchResults" aria-label="Suchergebnisse">
              <li
                v-for="(searchResult, index) in searchResults.member"
                :key="getIdentifier(searchResult.item)"
                class="my-40"
              >
                <SearchResult :search-result :order="index" />
              </li>
            </ul>

            <div
              v-if="isLoading"
              class="flex h-full min-h-48 w-full items-center justify-center"
            >
              <UiProgressSpinner />
            </div>
          </Pagination>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>
