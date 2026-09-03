<script setup lang="ts">
import type {
  SearchResultHeaderItem,
  TextHeaderItem,
} from "~/components/search/SearchResultHeader.vue";
import type { AdministrativeDirective, SearchResult } from "~/types/api";
import type { SearchResultHeadingLevel } from "~/utils/search/searchResults";
import {
  getMatch,
  getSearchResultHeadline,
  getTitleWithFallback,
} from "~/utils/search/searchResults";

const {
  searchResult,
  order,
  headingLevel = "2",
} = defineProps<{
  searchResult: SearchResult<AdministrativeDirective>;
  order: number;

  /** Heading level of the result title. */
  headingLevel?: SearchResultHeadingLevel;
}>();

const headlineStyle = computed(() => getSearchResultHeadline(headingLevel));

const { searchResultClicked } = usePostHog();

const router = useRouter();
const route = useRoute();

const fields = new Map([
  ["tableOfContentsEntries", { id: "inhalt", title: "Inhalt" }],
  ["shortReport", { id: "kurzreferat", title: "Kurzreferat" }],
]);

const headline = computed(() =>
  getTitleWithFallback(
    getMatch("headline", searchResult.textMatches),
    searchResult.item.headline,
  ),
);

const resultTypeId = useId();

const headerItems = computed(() => {
  const item = searchResult.item;

  const items: SearchResultHeaderItem[] = [];

  if (item.legislationAuthority) {
    items.push({ type: "text", value: item.legislationAuthority });
  }

  if (item.referenceNumbers?.[0]) {
    items.push({
      type: "badge",
      value: item.referenceNumbers?.[0],
      color: "gray",
    });
  }

  const formattedEntryIntoForce = dateFormattedDDMMYYYY(
    item.entryIntoForceDate,
  );
  if (formattedEntryIntoForce) {
    items.push({ type: "text", value: formattedEntryIntoForce });
  }

  const docTypeItem: TextHeaderItem = {
    type: "text",
    value: searchResult.item.documentType,
    id: resultTypeId,
  };

  return {
    documentType: docTypeItem,
    otherItems: items,
  };
});

const detailPageRoute = computed(() => ({
  name: "verwaltungsregelungen-documentNumber",
  params: {
    documentNumber: searchResult.item.documentNumber,
  },
  query: { from: route.fullPath },
}));

const previewSections = useSearchResultSections(
  () => searchResult.textMatches,
  fields,
);

function trackResultClick() {
  const url = router.resolve(detailPageRoute.value).href;
  searchResultClicked(url, order);
}
</script>

<template>
  <div class="flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader
      :document-type="headerItems.documentType"
      :items="headerItems.otherItems"
    />
    <NuxtLink
      :to="detailPageRoute"
      :aria-describedby="resultTypeId"
      :class="headlineStyle.class"
      @click="trackResultClick()"
    >
      <component :is="headlineStyle.tag">
        <span v-html="headline" />
      </component>
    </NuxtLink>

    <div v-if="previewSections.length" class="flex w-full flex-col gap-6">
      <div v-for="section in previewSections" :key="section.id">
        <NuxtLink
          :to="{ ...detailPageRoute, hash: `#${section.id}` }"
          class="ris-link1-bold link-hover"
          external
          @click="trackResultClick()"
          >{{ section.title }}:</NuxtLink
        >{{ " " }}
        <span
          v-if="section.text"
          data-testid="highlighted-field"
          class="ris-label1-regular"
          v-html="section.text"
        />
      </div>
    </div>
  </div>
</template>
