<script setup lang="ts">
import type { SearchResultHeaderItem } from "~/components/Search/SearchResultHeader.vue";
import { usePostHogStore } from "~/stores/usePostHogStore";
import type { Literature, SearchResult, TextMatch } from "~/types";
import { LITERATURE_TITLE_PLACEHOLDER } from "~/utils/literature";
import { sanitizeSearchResult } from "~/utils/sanitize";
import { addEllipsis } from "~/utils/textFormatting";
import OutlineBookIcon from "~icons/ic/outline-book";

const postHogStore = usePostHogStore();
const router = useRouter();

const props = defineProps<{
  searchResult: SearchResult<Literature>;
  order: number;
}>();

type LiteratureMetadata = {
  headline: string;
  alternativeHeadline: string;
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

const detailPageRoute = computed(() => ({
  name: "literature-documentNumber",
  params: { documentNumber: props.searchResult.item.documentNumber },
}));

const metadata = computed(() => {
  const item = props.searchResult.item;
  return {
    headline:
      getMatch("mainTitle", props.searchResult.textMatches) || item.headline,
    alternativeHeadline:
      getMatch("documentaryTitle", props.searchResult.textMatches) ||
      item.alternativeHeadline,
    shortReport: getShortReportSnippet(
      item.shortReport,
      props.searchResult.textMatches,
    ),
  } as LiteratureMetadata;
});

const resultTypeId = useId();

const headerItems = computed<SearchResultHeaderItem[]>(() => {
  const item = props.searchResult.item;
  const reference =
    item.dependentReferences?.[0] ?? item.independentReferences?.[0];
  return [
    { value: item.documentTypes?.[0], id: resultTypeId },
    { value: reference },
    { value: item.yearsOfPublication?.[0] },
  ].filter((item): item is SearchResultHeaderItem => item.value !== undefined);
});

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

function trackResultClick() {
  const url = router.resolve(detailPageRoute.value).href;
  postHogStore.searchResultClicked(url, props.order);
}
</script>

<template>
  <div class="my-36 flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader :icon="OutlineBookIcon" :items="headerItems" />
    <NuxtLink
      :to="detailPageRoute"
      :aria-describedby="resultTypeId"
      class="ris-heading3-bold! ris-link1-regular max-w-title link-hover block"
      @click="trackResultClick()"
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
