<script setup lang="ts">
import { Message, ProgressSpinner, Select } from "primevue";
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
          :to="{ name: 'advanced-search' }"
          class="ris-link2-bold 2xl:ris-link1-bold"
        >
          Erweiterte Suche
        </NuxtLink>
      </p>

      <div class="content-grid mt-32 gap-y-32">
        <div
          class="col-span-12 row-start-2 flex flex-wrap items-center justify-between gap-x-32 gap-y-16 md:row-auto"
        >
          <output
            aria-atomic="true"
            aria-live="polite"
            class="typo-label1-bold"
          >
            {{ isLoading ? "Lade ..." : formattedResultCount }}
          </output>

          <div class="flex flex-wrap gap-x-32 gap-y-16">
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
        </div>

        <aside
          class="col-span-12 pb-10 md:col-span-4 lg:col-span-3"
          :aria-labelledby="filterHeadingId"
        >
          <h2
            :id="filterHeadingId"
            class="typo-label1-regular mb-16 flex items-center"
          >
            Filter
          </h2>

          <div class="flex flex-col gap-24">
            <SearchCategoryFilter
              :model-value="categoryFilterValue"
              @update:model-value="updateCategoryFilter"
            />

            <SearchCourtFilter
              v-if="documentKind === DocumentKind.CaseLaw"
              :model-value="court"
              @update:model-value="updateCourt"
            />

            <SearchDateRangeFilter
              v-if="
                documentKind === DocumentKind.CaseLaw ||
                documentKind === DocumentKind.AdministrativeDirective
              "
              v-model="localDateFilter"
              @update:model-value="updateDateFilter"
            />

            <SearchYearRangeFilter
              v-else-if="documentKind === DocumentKind.Literature"
              v-model="localDateFilter"
              @update:model-value="updateDateFilter"
            />
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
              <ProgressSpinner />
            </div>
          </Pagination>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>
