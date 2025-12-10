<script setup lang="ts">
import RuleIcon from "virtual:icons/ic/outline-rule";
import type { SearchResultHeaderItem } from "~/components/Search/SearchResultHeader.vue";
import { usePostHogStore } from "~/stores/usePostHogStore";
import type { AdministrativeDirective, SearchResult } from "~/types";
import { ADMINISTRATIVE_DIRECTIVE_TITLE_PLACEHOLDER } from "~/utils/administrativeDirective";
import { sanitizeSearchResult } from "~/utils/sanitize";

const postHogStore = usePostHogStore();
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
  sanitizeSearchResult(
    getMatch("headline") || ADMINISTRATIVE_DIRECTIVE_TITLE_PLACEHOLDER,
  ),
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
  postHogStore.searchResultClicked(url, order);
}
</script>

<template>
  <div class="ris-body1-regular my-36 flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader :icon="RuleIcon" :items="headerItems" />
    <NuxtLink
      :to="detailPageRoute"
      :aria-describedby="resultTypeId"
      class="ris-heading3-bold! ris-link1-regular max-w-title link-hover block"
      @click="trackResultClick()"
    >
      <h2>
        <span v-html="headline" />
      </h2>
    </NuxtLink>

    <div v-if="text" class="flex w-full max-w-prose flex-col gap-6">
      <span data-testid="highlighted-field" v-html="text"> </span>
    </div>
  </div>
</template>
