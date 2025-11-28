<script setup lang="ts">
import RuleIcon from "virtual:icons/ic/outline-rule";
import type { SearchResultHeaderItem } from "~/components/Search/SearchResultHeader.vue";
import { usePostHogStore } from "~/stores/usePostHogStore";
import type { AdministrativeDirective, SearchResult } from "~/types";
import { ADMINISTRATIVE_DIRECTIVE_TITLE_PLACEHOLDER } from "~/utils/administrativeDirective";
import { sanitizeSearchResult } from "~/utils/sanitize";

const postHogStore = usePostHogStore();

const { searchResult, order } = defineProps<{
  searchResult: SearchResult<AdministrativeDirective>;
  order: number;
}>();

const detailPageUrl = computed(
  () => `/administrative-directive/${searchResult.item.documentNumber}`,
);

const headerItems = computed(() => {
  const item = searchResult.item;
  return [
    { value: item.documentType },
    { value: item.legislationAuthority },
    { value: item.referenceNumbers?.[0] },
    { value: dateFormattedDDMMYYYY(item.entryIntoForceDate) },
  ].filter((item) => item.value !== undefined) as SearchResultHeaderItem[];
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
  postHogStore.searchResultClicked(detailPageUrl.value, order);
}
</script>

<template>
  <div class="my-36 flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader :icon="RuleIcon" :items="headerItems" />
    <NuxtLink
      :to="detailPageUrl"
      class="ris-heading3-bold max-w-title link-hover block text-blue-800"
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
