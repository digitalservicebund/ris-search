<script setup lang="ts">
import OutlineBookIcon from "virtual:icons/ic/outline-book";
import { usePostHogStore } from "~/stores/usePostHogStore";
import type { Literature, SearchResult, TextMatch } from "~/types";
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
  documentType: string;
  dependentReference: string;
  yearOfPublication: string;
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
      getMatch("headline", props.searchResult.textMatches) || item.headline,
    alternativeHeadline:
      getMatch("documentaryTitle", props.searchResult.textMatches) ||
      item.alternativeHeadline,
    url: `/literature/${props.searchResult.item.documentNumber}`,
    documentType: item.documentTypes?.at(0),
    dependentReference: item.dependentReferences?.at(0),
    yearOfPublication: item.yearsOfPublication?.at(0),
    shortReport: getShortReportSnippet(
      item.shortReport,
      props.searchResult.textMatches,
    ),
  } as LiteratureMetadata;
});

function trackResultClick(url: string) {
  postHogStore.searchResultClicked(url, props.order);
}

const sanitizedHeadline = computed(() =>
  sanitizeSearchResult(
    metadata.value.headline || metadata.value.alternativeHeadline,
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
  <div class="my-36 hyphens-auto" data-testid="searchResult">
    <div class="ris-label2-regular flex flex-row flex-wrap items-center gap-8">
      <div class="flex items-center">
        <OutlineBookIcon class="mr-4 h-[1rem] text-gray-900" />
        <span>
          {{ metadata.documentType }}
        </span>
      </div>
      <span>{{ metadata.dependentReference }}</span>
      <span>{{ metadata.yearOfPublication }}</span>
    </div>
    <NuxtLink
      :to="metadata.url"
      class="ris-heading3-bold max-w-title link-hover mt-8 block text-balance text-blue-800"
      @click="trackResultClick(metadata.url)"
    >
      <h2>
        <span v-html="sanitizedHeadline" />
      </h2>
    </NuxtLink>
    <div class="mt-6 flex w-full max-w-prose flex-col gap-6">
      <span
        data-testid="highlighted-field"
        :class="{ 'line-clamp-3': !shortReportIncludesHighlight }"
        v-html="sanitizedShortReport"
      />
    </div>
  </div>
</template>
