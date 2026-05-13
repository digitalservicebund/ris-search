<script setup lang="ts">
import { Message, Select } from "primevue";
import { DocumentKind } from "~/types/api";
import { isStrictDateFilterValue } from "~/utils/search/dateFilterType";
import { useSearchSeo } from "~/composables/useSearchSeo";

useSkipLinks([
  { label: "Zur Suche", to: "#search" },
  { label: "Zum Inhalt", to: "#main" },
  { label: "Zum Fußbereich", to: "#footer" },
]);

const filterHeadingId = useId();

const {
  court,
  dateFilter,
  documentKind,
  itemsPerPage,
  pageIndex,
  query,
  saveFilterStateToRoute,
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

const strictDateFilter = ref(
  isStrictDateFilterValue(dateFilter.value) ? dateFilter.value : undefined,
);

watch(dateFilter, (newVal) => {
  if (isStrictDateFilterValue(newVal)) strictDateFilter.value = newVal;
});

// Document kind -------------------------------------------

const categoryFilterValue = computed({
  get() {
    let val = documentKind.value.toString();
    if (typeGroup.value) val += `.${typeGroup.value}`;
    return val;
  },
  set(value) {
    const [maybeKind, group] = value.split(".");

    let kind = DocumentKind.All;
    if (maybeKind && isDocumentKind(maybeKind)) kind = maybeKind;
    documentKind.value = kind;

    typeGroup.value = group;
  },
});

const documentKindAndGroup = computed(() => ({
  documentKind: documentKind.value,
  typeGroup: typeGroup.value,
}));

// Search results ------------------------------------------

const itemsPerPageLabelId = useId();
const resultsContainerRef = ref<HTMLElement | null>(null);
const scrollToResultsOnLoad = ref(false);

const itemsPerPageOptions = ["10", "50", "100"];

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

const formattedResultCount = computed(() => {
  if (isLoading.value) return "";
  return formatResultCount(totalItemCount.value);
});

const isLoading = computed(() => searchStatus.value === "pending");

// Auto reload for "discrete" actions
watch(
  () => [
    court.value,
    documentKind.value,
    itemsPerPage.value,
    pageIndex.value,
    sort.value,
    strictDateFilter.value,
    typeGroup.value,
    query.value,
  ],
  async () => {
    await submit();
  },
);

async function submit() {
  await saveFilterStateToRoute();
  await submitSearch();
}

async function updatePage(page: number) {
  scrollToResultsOnLoad.value = true;
  pageIndex.value = page;
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
  <Breadcrumbs :items="[{ label: 'Suche' }]" />

  <div class="py-24">
    <h1 class="ris-heading2-bold inline-block">Suche</h1>
  </div>

  <div id="search">
    <SearchSimpleSearchInput v-model="query" />
  </div>

  <SkipLink to="#search-results" class="mt-8">Zu den Ergebnissen</SkipLink>

  <p v-if="privateFeaturesEnabled" class="ris-label2-regular mt-8">
    Mehr Suchoptionen finden Sie unter
    <NuxtLink :to="{ name: 'advanced-search' }" class="ris-link2-bold">
      Erweiterte Suche
    </NuxtLink>
  </p>

  <div class="mt-24 flex flex-col gap-48 lg:flex-row">
    <aside class="pb-10 lg:w-3/12" :aria-labelledby="filterHeadingId">
      <h2
        :id="filterHeadingId"
        class="ris-label1-regular flex h-48 items-center"
      >
        Filter
      </h2>

      <div class="flex flex-col gap-24">
        <SearchCategoryFilter v-model="categoryFilterValue" />

        <SearchCourtFilter
          v-if="documentKind === DocumentKind.CaseLaw"
          v-model="court"
        />

        <SearchDateRangeFilter
          v-if="
            documentKind === DocumentKind.CaseLaw ||
            documentKind === DocumentKind.AdministrativeDirective
          "
          v-model="dateFilter"
        />

        <SearchYearRangeFilter
          v-else-if="documentKind === DocumentKind.Literature"
          v-model="dateFilter"
        />
      </div>
    </aside>

    <div
      id="search-results"
      ref="resultsContainerRef"
      class="w-full scroll-mt-16 flex-col justify-end gap-8 lg:w-9/12"
    >
      <Pagination
        :is-loading="isLoading"
        :page="searchResults"
        navigation-position="bottom"
        @update-page="updatePage"
      >
        <div
          class="mb-12 flex w-full flex-wrap items-center justify-between gap-x-32 gap-y-16"
        >
          <output
            aria-atomic="true"
            aria-live="polite"
            class="ris-label2-regular"
          >
            {{ isLoading ? "Lade ..." : formattedResultCount }}
          </output>

          <div class="flex flex-wrap gap-x-32 gap-y-16">
            <div class="flex items-center gap-8">
              <label :id="itemsPerPageLabelId" class="ris-label2-regular">
                Einträge pro Seite
              </label>
              <Select
                v-model="itemsPerPage"
                :aria-labelledby="itemsPerPageLabelId"
                :options="itemsPerPageOptions"
              />
            </div>

            <SearchSortSelect v-model="sort" :document-kind />
          </div>
        </div>

        <div class="max-w-prose">
          <Message v-if="!!searchError" severity="error">
            {{ searchError.message }}
          </Message>

          <Message
            severity="warn"
            class="ris-body2-regular mt-16 max-w-prose"
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
            >
              <SearchResult :search-result :order="index" />
            </li>
          </ul>

          <div
            v-if="isLoading"
            class="flex h-full min-h-48 w-full items-center justify-center"
          >
            <DelayedLoadingMessage />
          </div>
        </div>
      </Pagination>
    </div>
  </div>
</template>
