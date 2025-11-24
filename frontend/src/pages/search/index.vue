<script setup lang="ts">
import { storeToRefs } from "pinia";
import Message from "primevue/message";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import type { Page } from "~/components/Pagination/Pagination.vue";
import CategoryFilter from "~/components/Search/CategoryFilter.vue";
import CourtFilter from "~/components/Search/CourtFilter.vue";
import DateRangeFilter from "~/components/Search/DateRangeFilter.vue";
import ItemsPerPageDropdown from "~/components/Search/ItemsPerPageDropdown.vue";
import SearchResult from "~/components/Search/SearchResult.vue";
import SimpleSearchInput from "~/components/Search/SimpleSearchInput.vue";
import SortSelect from "~/components/Search/SortSelect.vue";
import YearRangeFilter from "~/components/Search/YearRangeFilter.vue";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";
import { useSimpleSearchParamsStore } from "~/stores/searchParams";
import { DocumentKind } from "~/types";
import { buildResultCountString } from "~/utils/pagination";
import {
  categoryToDocumentKind,
  convertParams,
  getUrl,
} from "~/utils/search/simpleSearch";

useStaticPageSeo("suche");

const store = useSimpleSearchParamsStore();
const values = storeToRefs(store);

const params = computed(() => convertParams(values.params.value));

const {
  data,
  error: loadError, // must not be named error, refer to https://github.com/nuxt/test-utils/issues/684#issuecomment-1946138626
  status,
  execute,
} = await useRisBackend<Page>(() => getUrl(values.category.value), {
  query: params,
  watch: [params],
});

watch(
  loadError,
  () => {
    if (loadError.value) {
      showError(loadError.value);
    }
  },
  { immediate: true },
);

const isLoading = computed(() => status.value === "pending");
const currentPage = computed(() => data.value);

async function handleSearchSubmit(value?: string) {
  store.setQuery(value ?? "");
  store.setPageNumber(0);
  await execute();
}

async function updatePage(page: number) {
  store.setPageNumber(page);
}

const documentKind = computed(() =>
  categoryToDocumentKind(values.category.value),
);

const title = computed(() => {
  if (store.query) {
    return `${store.query} — Suche`;
  } else {
    switch (documentKind.value) {
      case DocumentKind.Norm:
        return "Gesetze & Verordnungen — Suche";
      case DocumentKind.CaseLaw:
        return "Rechtsprechung — Suche";
      case DocumentKind.Literature:
        return "Literatur — Suche";
      default:
        return "Suche";
    }
  }
});

useHead({ title });

const privateFeaturesEnabled = usePrivateFeaturesFlag();
</script>

<template>
  <ContentWrapper>
    <div class="pb-24">
      <h1 class="ris-heading2-regular inline-block font-semibold">Suche</h1>
    </div>
    <SimpleSearchInput
      :model-value="values.query.value"
      @update:model-value="handleSearchSubmit"
    />
    <p v-if="privateFeaturesEnabled" class="ris-label2-regular mt-8">
      Mehr Suchoptionen finden sie unter
      <NuxtLink :to="{ name: 'advanced-search' }" class="ris-link2-bold">
        Erweiterte Suche
      </NuxtLink>
    </p>

    <NuxtLink :to="{ hash: '#main' }" class="sr-only focus:not-sr-only">
      Zu den Hauptinhalten springen
    </NuxtLink>
    <div class="mt-24 flex flex-col gap-48 pb-24 lg:flex-row">
      <fieldset
        class="top-8 flex w-full flex-col gap-24 pb-10 lg:sticky lg:max-h-screen lg:w-3/12 lg:overflow-y-auto"
      >
        <legend class="ris-label1-regular flex h-48 items-center">
          Filter
        </legend>
        <CategoryFilter />
        <CourtFilter />
        <DateRangeFilter v-if="documentKind === DocumentKind.CaseLaw" />
        <YearRangeFilter v-else-if="documentKind === DocumentKind.Literature" />
      </fieldset>
      <div id="main" class="w-full flex-col justify-end gap-8 lg:w-9/12">
        <Pagination
          :is-loading="isLoading"
          navigation-position="bottom"
          :page="currentPage"
          @update-page="updatePage"
        >
          <div
            class="mb-12 flex w-full flex-wrap items-center justify-between gap-x-32 gap-y-16"
          >
            <output
              id="result-count"
              aria-live="polite"
              aria-atomic="true"
              class="ris-label2-regular"
            >
              {{ isLoading ? "Lade ..." : null }}
              {{
                currentPage && !isLoading
                  ? buildResultCountString(currentPage)
                  : ""
              }}
            </output>
            <div class="flex flex-wrap gap-x-32 gap-y-16">
              <ItemsPerPageDropdown />
              <SortSelect v-model="store.sort" :document-kind="documentKind" />
            </div>
          </div>
          <p
            v-if="!!loadError"
            class="my-8 text-red-700"
            role="alert"
            aria-relevant="all"
          >
            {{ loadError.message }}
          </p>
          <Message severity="warn" class="ris-body2-regular mt-16 max-w-prose">
            <p class="ris-body2-bold mt-2">
              Dieser Service befindet sich in der Testphase:
            </p>
            <p>
              Der Datenbestand ist noch nicht vollständig und die
              Suchpriorisierung noch nicht final. Der Service ist in
              Entwicklung. Wir arbeiten an der Ergänzung und Darstellung aller
              Inhalte.
            </p>
          </Message>
          <ul
            v-if="currentPage && currentPage?.member?.length > 0"
            aria-label="Suchergebnisse"
            class="w-full"
          >
            <li v-for="(element, index) in currentPage.member" :key="index">
              <SearchResult :search-result="element" :order="index" />
            </li>
          </ul>
          <div
            v-if="isLoading"
            class="flex h-full min-h-48 w-full items-center justify-center"
          >
            <DelayedLoadingMessage />
          </div>
        </Pagination>
      </div>
    </div>
  </ContentWrapper>
</template>
