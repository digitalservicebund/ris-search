<script setup lang="ts">
import OutlineBookIcon from "virtual:icons/ic/outline-book";
import { usePostHogStore } from "~/stores/usePostHogStore";
import type { Literature, SearchResult, TextMatch } from "~/types";
import { sanitizeSearchResult } from "~/utils/sanitize";

const postHogStore = usePostHogStore();

const props = defineProps<{
  searchResult: SearchResult<Literature>;
  order: number;
}>();

type LiteratureMetadata = {
  headline: string;
  documentaryTitle: string;
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
  fullText: string | null,
  matches: TextMatch[] | undefined,
): string | null {
  if (!fullText) return null;

  const match = matches?.find((m) => m.name === "shortReport");
  if (!match?.text?.includes("<mark>")) return fullText;

  const keywordMatch = match.text.match(/<mark>(.*?)<\/mark>/);
  if (!keywordMatch) return fullText;

  const keyword = keywordMatch[1];
  const index = fullText.toLowerCase().indexOf(keyword.toLowerCase());
  if (index === -1) return match.text;

  const desiredLength = 270;
  const start = Math.max(
    0,
    Math.min(
      index - Math.floor(desiredLength / 2),
      fullText.length - desiredLength,
    ),
  );
  const end = Math.min(fullText.length, start + desiredLength);

  let snippet = fullText.slice(start, end).trim();

  if (start > 0) snippet = "… " + snippet;
  if (end < fullText.length) snippet += " …";

  const escapedKeyword = keyword.replaceAll(
    /[.*+?^${}()|[\]\\]/g,
    String.raw`\$&`,
  );
  const regex = new RegExp(`(${escapedKeyword})`, "gi");

  return snippet.replace(regex, "<mark>$1</mark>");
}

const metadata = computed(() => {
  const item = props.searchResult.item;
  return {
    headline:
      getMatch("headline", props.searchResult.textMatches) || item.headline,
    documentaryTitle:
      getMatch("documentaryTitle", props.searchResult.textMatches) ||
      item.documentaryTitle,
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
        <span
          v-html="
            sanitizeSearchResult(metadata.headline || metadata.documentaryTitle)
          "
        />
      </h2>
    </NuxtLink>
    <div class="mt-6 flex w-full max-w-prose flex-col gap-6">
      <span
        data-testid="highlighted-field"
        :class="{ 'line-clamp-3': !metadata.shortReport?.includes('<mark>') }"
        v-html="sanitizeSearchResult(metadata.shortReport)"
      />
    </div>
  </div>
</template>
