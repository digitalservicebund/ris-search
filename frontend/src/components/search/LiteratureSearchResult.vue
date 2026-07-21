<script setup lang="ts">
import OutlineBookIcon from "~icons/ic/outline-book";
import type { SearchResultHeaderItem } from "~/components/search/SearchResultHeader.vue";
import type { Literature, SearchResult } from "~/types/api";
import { getMatch, getTitleWithFallback } from "~/utils/search/searchResults";

const { searchResult, order } = defineProps<{
  searchResult: SearchResult<Literature>;
  order: number;
}>();

const { searchResultClicked } = usePostHog();

const router = useRouter();
const route = useRoute();

const fields = new Map([
  ["outline", { id: "gliederung", title: "Gliederung" }],
  ["shortReport", { id: "kurzreferat", title: "Kurzreferat" }],
]);

const headline = computed(() =>
  getTitleWithFallback(
    getMatch("mainTitle", searchResult.textMatches),
    searchResult.item.headline,
    getMatch("documentaryTitle", searchResult.textMatches),
    searchResult.item.alternativeHeadline,
  ),
);

const resultTypeId = useId();

const headerItems = computed<SearchResultHeaderItem[]>(() => {
  const item = searchResult.item;
  const reference =
    item.dependentReferences?.[0] ?? item.independentReferences?.[0];
  return [
    { value: item.documentTypes?.[0], id: resultTypeId },
    { value: reference },
    { value: item.yearsOfPublication?.[0] },
  ].filter((i): i is SearchResultHeaderItem => i.value !== undefined);
});

const detailPageRoute = computed(() => ({
  name: "literaturnachweise-documentNumber",
  params: { documentNumber: searchResult.item.documentNumber },
  query: { from: route.fullPath },
}));

const previewSections = useSearchResultSections(
  () => searchResult.textMatches,
  fields,
);

function trackResultClick() {
  const url = router.resolve(detailPageRoute.value).href;
  searchResultClicked(url, order);
}
</script>

<template>
  <div class="flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader :icon="OutlineBookIcon" :items="headerItems" />
    <NuxtLink
      :to="detailPageRoute"
      :aria-describedby="resultTypeId"
      class="typo-headline-searchresult"
      @click="trackResultClick()"
    >
      <h2>
        <span v-html="headline" />
      </h2>
    </NuxtLink>

    <div v-if="previewSections.length" class="flex w-full flex-col gap-6">
      <div v-for="section in previewSections" :key="section.id">
        <NuxtLink
          :to="{ ...detailPageRoute, hash: `#${section.id}` }"
          class="ris-link1-bold link-hover"
          external
          @click="trackResultClick()"
          >{{ section.title }}:</NuxtLink
        >{{ " " }}
        <span
          v-if="section.text"
          data-testid="highlighted-field"
          class="ris-label1-regular"
          v-html="section.text"
        />
      </div>
    </div>
  </div>
</template>
