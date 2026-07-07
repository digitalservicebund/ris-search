<script setup lang="ts">
import GavelIcon from "~icons/ic/outline-gavel";
import type { SearchResultHeaderItem } from "~/components/search/SearchResultHeader.vue";
import type { CaseLaw, SearchResult } from "~/types/api";
import {
  getMatch,
  getMatches,
  getTitleWithFallback,
} from "~/utils/search/searchResults";

const { searchResult, order } = defineProps<{
  searchResult: SearchResult<CaseLaw>;
  order: number;
}>();

const { searchResultClicked } = usePostHog();

const router = useRouter();
const route = useRoute();

const fields = new Map([
  ["guidingPrinciple", { id: "leitsatz", title: "Leitsatz" }],
  ["headnote", { id: "orientierungssatz", title: "Orientierungssatz" }],
  [
    "otherHeadnote",
    {
      id: "sonstiger-orientierungssatz",
      title: "Sonstiger Orientierungssatz",
    },
  ],
  ["tenor", { id: "tenor", title: "Tenor" }],
  ["grounds", { id: "gruende", title: "Gründe" }],
  ["caseFacts", { id: "tatbestand", title: "Tatbestand" }],
  [
    "decisionGrounds",
    { id: "entscheidungsgruende", title: "Entscheidungsgründe" },
  ],
]);

const headline = computed(() =>
  getTitleWithFallback(
    removeOuterParentheses(getMatch("headline", searchResult.textMatches)),
    removeOuterParentheses(searchResult.item.headline),
  ),
);

const decisionName = computed(() => searchResult.item.decisionName?.at(0));

const resultTypeId = useId();

const headerItems = computed(() => {
  const item = searchResult.item;

  const items: SearchResultHeaderItem[] = [
    { value: item.documentType || "Entscheidung", id: resultTypeId },
  ];

  if (item.courtName) items.push({ value: item.courtName });

  const formattedDate = dateFormattedDDMMYYYY(item.decisionDate);
  if (formattedDate) items.push({ value: formattedDate });

  const fileNumbers = getFileNumbers(item);
  if (fileNumbers) items.push({ value: fileNumbers, isMarkup: true });

  return items;
});

function getFileNumbers(item: CaseLaw) {
  const matches = getMatches("fileNumbers", searchResult.textMatches);

  if (matches.length) {
    const replaced = [...item.fileNumbers];
    for (const match of matches) {
      const stripped = stripAllHtml(match);
      const index = item.fileNumbers.indexOf(stripped);
      if (index !== -1) {
        replaced[index] = match;
      }
    }

    return replaced.join(", ");
  }

  return item.fileNumbers?.join(", ");
}

const detailPageRoute = computed(() => ({
  name: "case-law-documentNumber",
  params: { documentNumber: searchResult.item.documentNumber },
  query: { from: route.fullPath },
}));

const previewSections = useSearchResultSections(
  () => searchResult.textMatches,
  fields,
  4,
);

function trackResultClick() {
  const url = router.resolve(detailPageRoute.value).href;
  searchResultClicked(url, order);
}
</script>

<template>
  <div class="flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader :icon="GavelIcon" :items="headerItems" />
    <NuxtLink
      :to="detailPageRoute"
      :aria-describedby="resultTypeId"
      class="typo-headline-searchresult"
      @click="trackResultClick()"
    >
      <h2>
        <span v-if="!!decisionName"> {{ decisionName }} — </span>
        <span v-html="headline" />
      </h2>
    </NuxtLink>

    <div v-if="previewSections.length" class="flex w-full flex-col gap-6">
      <div v-for="section in previewSections" :key="section?.id">
        <NuxtLink
          :to="{ ...detailPageRoute, hash: `#${section?.id}` }"
          class="typo-link-bold link-hover"
          external
          @click="trackResultClick()"
          >{{ section?.title }}:</NuxtLink
        >{{ " " }}
        <span
          v-if="section.text"
          data-testid="highlighted-field"
          class="typo-label1-regular"
          v-html="section.text"
        />
      </div>
    </div>
  </div>
</template>
