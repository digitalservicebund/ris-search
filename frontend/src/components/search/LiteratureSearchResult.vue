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
  name: "literature-documentNumber",
  params: { documentNumber: searchResult.item.documentNumber },
}));

const shortReport = computed(() => {
  const fullText = searchResult.item.shortReport;
  if (!fullText) return undefined;

  // Find the relevant highlight for "shortReport"
  const match = searchResult.textMatches.find(
    (hl) => hl.name === "shortReport" && hl.text?.includes("<mark>"),
  );

  // No highlight — just return the whole text, will be truncated with
  // line-clamp because of 3 lines wanted and not only 2
  if (!match) return fullText;

  // Return the highlighted text (with ellipsis if needed)
  return addEllipsis(match.text);
});

const shortReportIncludesHighlight = computed(
  () => shortReport.value?.includes("<mark>") ?? false,
);

const sanitizedShortReport = computed(() =>
  sanitizeSearchResult(shortReport.value ?? ""),
);

function trackResultClick() {
  const url = router.resolve(detailPageRoute.value).href;
  searchResultClicked(url, order);
}
</script>

<template>
  <div class="my-36 flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader :icon="OutlineBookIcon" :items="headerItems" />
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

    <div class="flex w-full flex-col gap-6">
      <span
        data-testid="highlighted-field"
        :class="{ 'line-clamp-3': !shortReportIncludesHighlight }"
        v-html="sanitizedShortReport"
      />
    </div>
  </div>
</template>
