<script setup lang="ts">
import RuleIcon from "~icons/ic/outline-rule";
import type { SearchResultHeaderItem } from "~/components/search/SearchResultHeader.vue";
import type { AdministrativeDirective, SearchResult } from "~/types/api";
import { getMatch, getTitleWithFallback } from "~/utils/search/searchResults";

const { searchResult, order } = defineProps<{
  searchResult: SearchResult<AdministrativeDirective>;
  order: number;
}>();

const { searchResultClicked } = usePostHog();

const router = useRouter();
const route = useRoute();

const fields = new Map([
  ["tableOfContentsEntries", { id: "inhalt", title: "Inhalt" }],
  ["shortReport", { id: "kurzreferat", title: "Kurzreferat" }],
]);

const headline = computed(() =>
  getTitleWithFallback(
    getMatch("headline", searchResult.textMatches),
    searchResult.item.headline,
  ),
);

const resultTypeId = useId();

const headerItems = computed<SearchResultHeaderItem[]>(() => {
  const item = searchResult.item;
  return [
    { value: item.documentType, id: resultTypeId },
    { value: item.legislationAuthority },
    { value: item.referenceNumbers?.[0] },
    { value: dateFormattedDDMMYYYY(item.entryIntoForceDate) },
  ].filter((i): i is SearchResultHeaderItem => i.value !== undefined);
});

const detailPageRoute = computed(() => ({
  name: "administrative-directives-documentNumber",
  params: {
    documentNumber: searchResult.item.documentNumber,
  },
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
    <SearchResultHeader :icon="RuleIcon" :items="headerItems" />
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
