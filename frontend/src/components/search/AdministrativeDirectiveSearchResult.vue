<script setup lang="ts">
import RuleIcon from "~icons/ic/outline-rule";
import type { SearchResultHeaderItem } from "~/components/search/SearchResultHeader.vue";
import { usePostHog } from "~/composables/usePostHog";
import type { AdministrativeDirective, SearchResult } from "~/types/api";
import { getMatch, getTitleWithFallback } from "~/utils/search/searchResults";

const { searchResult, order } = defineProps<{
  searchResult: SearchResult<AdministrativeDirective>;
  order: number;
}>();

const { searchResultClicked } = usePostHog();

const router = useRouter();

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
}));

const text = computed(() => {
  const shortReportMatch = getMatch("shortReport", searchResult.textMatches);
  if (!shortReportMatch) return undefined;

  const plainShortReportMatch = stripAllHtml(shortReportMatch);
  const fullShortReport = searchResult.item.shortReport;
  const sanitizedShortReport = sanitizeSearchResult(shortReportMatch);

  if (plainShortReportMatch === fullShortReport) return sanitizedShortReport;

  const prefix = fullShortReport?.startsWith(plainShortReportMatch) ? "" : "… ";
  const postfix = fullShortReport?.endsWith(plainShortReportMatch) ? "" : " …";

  return `${prefix}${sanitizedShortReport}${postfix}`;
});

function trackResultClick() {
  const url = router.resolve(detailPageRoute.value).href;
  searchResultClicked(url, order);
}
</script>

<template>
  <div class="my-36 flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader :icon="RuleIcon" :items="headerItems" />
    <NuxtLink
      :to="detailPageRoute"
      :aria-describedby="resultTypeId"
      class="typo-headline3-bold! typo-link-regular link-hover block"
      @click="trackResultClick()"
    >
      <h2>
        <span v-html="headline" />
      </h2>
    </NuxtLink>

    <div v-if="text" class="flex w-full flex-col gap-6">
      <span data-testid="highlighted-field" v-html="text"> </span>
    </div>
  </div>
</template>
