<script setup lang="ts">
import type { SearchResultHeaderItem } from "~/components/search/SearchResultHeader.vue";
import { usePostHog } from "~/composables/usePostHog";
import type { AdministrativeDirective, SearchResult } from "~/types";
import { sanitizeSearchResult } from "~/utils/sanitize";
import RuleIcon from "~icons/ic/outline-rule";

const { searchResultClicked } = usePostHog();
const router = useRouter();

const { searchResult, order } = defineProps<{
  searchResult: SearchResult<AdministrativeDirective>;
  order: number;
}>();

const detailPageRoute = computed(() => ({
  name: "administrative-directives-documentNumber",
  params: {
    documentNumber: searchResult.item.documentNumber,
  },
}));

const resultTypeId = useId();

const headerItems = computed<SearchResultHeaderItem[]>(() => {
  const item = searchResult.item;
  return [
    { value: item.documentType, id: resultTypeId },
    { value: item.legislationAuthority },
    { value: item.referenceNumbers?.[0] },
    { value: dateFormattedDDMMYYYY(item.entryIntoForceDate) },
  ].filter((item): item is SearchResultHeaderItem => item.value !== undefined);
});

const headline = computed(() =>
  sanitizeSearchResult(getMatch("headline") || "Titelzeile nicht vorhanden"),
);

const text = computed(() => {
  const shortReportMatch = getMatch("shortReport");
  if (!shortReportMatch) return undefined;

  const plainShortReportMatch = sanitizeSearchResult(shortReportMatch, []);
  const fullShortReport = searchResult.item.shortReport;
  const sanitizedShortReport = sanitizeSearchResult(shortReportMatch);

  if (plainShortReportMatch === fullShortReport) return sanitizedShortReport;

  const prefix = fullShortReport?.startsWith(plainShortReportMatch) ? "" : "… ";
  const postfix = fullShortReport?.endsWith(plainShortReportMatch) ? "" : " …";

  return `${prefix}${sanitizedShortReport}${postfix}`;
});

function getMatch(name: string) {
  return searchResult.textMatches.find((match) => match.name === name)?.text;
}

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
      class="ris-heading3-bold! ris-link1-regular link-hover block"
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
