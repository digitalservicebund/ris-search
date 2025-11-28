<script setup lang="ts">
import OutlineBookIcon from "virtual:icons/ic/outline-book";
import type { SearchResultHeaderItem } from "~/components/Search/SearchResultHeader.vue";
import { usePostHogStore } from "~/stores/usePostHogStore";
import type { Literature, SearchResult, TextMatch } from "~/types";
import { LITERATURE_TITLE_PLACEHOLDER } from "~/utils/literature";
import { sanitizeSearchResult } from "~/utils/sanitize";
import { addEllipsis } from "~/utils/textFormatting";

const postHogStore = usePostHogStore();

const props = defineProps<{
  searchResult: SearchResult<Literature>;
  order: number;
}>();

type LiteratureMetadata = {
  headline: string;
  alternativeHeadline: string;
  url: string;
  shortReport: string;
};

function getMatch(match: string, highlights: TextMatch[]) {
  return highlights.find((highlight) => highlight.name === match)?.text;
}

function getShortReportSnippet(
  shortReport: string | null,
  textMatches: TextMatch[],
): string | undefined {
  if (!shortReport) return undefined;

  // Find the relevant highlight for "shortReport"
  const match = textMatches.find(
    (hl) => hl.name === "shortReport" && hl.text?.includes("<mark>"),
  );

  if (!match) {
    // No highlight — just return the whole text, will be truncated with line-clamp because of 3 lines wanted and not only 2
    return shortReport;
  }

  // Return the highlighted text (with ellipsis if needed)
  return addEllipsis(match.text);
}

const metadata = computed(() => {
  const item = props.searchResult.item;
  return {
    headline:
      getMatch("mainTitle", props.searchResult.textMatches) || item.headline,
    alternativeHeadline:
      getMatch("documentaryTitle", props.searchResult.textMatches) ||
      item.alternativeHeadline,
    url: `/literature/${props.searchResult.item.documentNumber}`,
    shortReport: getShortReportSnippet(
      item.shortReport,
      props.searchResult.textMatches,
    ),
  } as LiteratureMetadata;
});

const headerItems = computed(() => {
  const item = props.searchResult.item;
  return [
    { value: item.documentTypes?.at(0) },
    { value: item.dependentReferences?.at(0) },
    { value: item.yearsOfPublication?.at(0) },
  ].filter((value) => value !== undefined) as SearchResultHeaderItem[];
});

function trackResultClick(url: string) {
  postHogStore.searchResultClicked(url, props.order);
}

const sanitizedHeadline = computed(() =>
  sanitizeSearchResult(
    metadata.value.headline ||
      metadata.value.alternativeHeadline ||
      LITERATURE_TITLE_PLACEHOLDER,
  ),
);

const shortReportIncludesHighlight = computed(
  () => metadata.value.shortReport?.includes("<mark>") ?? false,
);

const sanitizedShortReport = computed(() =>
  sanitizeSearchResult(metadata.value.shortReport),
);
</script>

<template>
  <div class="my-36 flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader :icon="OutlineBookIcon" :items="headerItems" />
    <NuxtLink
      :to="metadata.url"
      class="ris-heading3-bold max-w-title link-hover block text-blue-800"
      @click="trackResultClick(metadata.url)"
    >
      <h2>
        <span v-html="sanitizedHeadline" />
      </h2>
    </NuxtLink>

    <div class="flex w-full max-w-prose flex-col gap-6">
      <span
        data-testid="highlighted-field"
        :class="{ 'line-clamp-3': !shortReportIncludesHighlight }"
        v-html="sanitizedShortReport"
      />
    </div>
  </div>
</template>
