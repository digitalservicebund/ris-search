<script setup lang="ts">
import type { RouteLocationRaw, RouteLocationAsPath } from "#vue-router";
import type {
  SearchResultHeaderItem,
  TextHeaderItem,
} from "~/components/search/SearchResultHeader.vue";
import type { LegislationExpression, SearchResult } from "~/types/api";
import { getMatch, getTitleWithFallback } from "~/utils/search/searchResults";

const { searchResult, order } = defineProps<{
  searchResult: SearchResult<LegislationExpression>;
  order: number;
}>();

const { searchResultClicked } = usePostHog();
const privateFeaturesEnabled = usePrivateFeaturesFlag();
const route = useRoute();

const headline = computed(() =>
  getTitleWithFallback(
    getMatch("name", searchResult.textMatches),
    searchResult.item.name,
  ),
);

const secondaryTitle = computed<TextHeaderItem | undefined>(() =>
  searchResult.item.alternateName
    ? {
        type: "text",
        value: truncateAtWord(searchResult.item.alternateName, 90, true),
      }
    : undefined,
);

const resultTypeId = useId();

const headerItems = computed(() => {
  const items: SearchResultHeaderItem[] = [];

  if (searchResult.item.abbreviation) {
    items.push({ type: "text", value: searchResult.item.abbreviation });
  }

  const validityStatus = formatNormValidity(searchResult.item.temporalCoverage);

  if (validityStatus) {
    items.push({
      type: "badge",
      value: validityStatus.label,
      color: validityStatus.color,
      class: "font-bold!",
    });
  }

  let dateValue: string | undefined = dateFormattedDDMMYYYY(
    searchResult.item.exampleOfWork.legislationDate,
  );

  if (privateFeaturesEnabled) {
    const coverage = temporalCoverageToValidityInterval(
      searchResult.item.temporalCoverage,
    );
    const from = dateFormattedDDMMYYYY(coverage?.from);
    const to = dateFormattedDDMMYYYY(coverage?.to);
    dateValue = from && to ? `${from} - ${to}` : from;
  }

  if (dateValue) {
    items.push({ type: "text", value: dateValue });
  }

  const docTypeItem: TextHeaderItem = {
    type: "text",
    value: "Norm",
    id: resultTypeId,
  };

  return {
    documentType: docTypeItem,
    otherItems: items,
  };
});

const detailPageRoute = computed<RouteLocationAsPath>(() => ({
  path: `/gesetze/${searchResult.item.legislationIdentifier}`,
  query: { from: route.fullPath },
}));

const relevantHighlights = computed(() =>
  searchResult.textMatches
    .filter((highlight) => highlight.name != "name")
    .map((hl) => {
      const textHasHighlight = hl.text.includes("<mark>");
      const text = textHasHighlight
        ? sanitizeSearchResult(addEllipsis(hl.text))
        : "";

      const highlightRoute: RouteLocationRaw = {
        ...detailPageRoute.value,
        path: `${detailPageRoute.value.path}/${hl.location}`,
      };

      return {
        location: hl.location,
        name: sanitizeSearchResult(hl.name),
        route: highlightRoute,
        text,
      };
    }),
);
</script>

<template>
  <div class="flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader
      :document-type="headerItems.documentType"
      :items="headerItems.otherItems"
      :secondary-item="secondaryTitle"
    >
    </SearchResultHeader>

    <NuxtLink
      v-if="detailPageRoute"
      :to="detailPageRoute"
      :aria-describedby="resultTypeId"
      class="typo-headline-searchresult"
      @click="searchResultClicked(detailPageRoute.path, order)"
    >
      <h2 v-html="headline" />
    </NuxtLink>

    <div
      v-if="relevantHighlights.length"
      class="flex w-full flex-col gap-6"
      data-testid="highlights"
    >
      <div
        v-for="(highlight, index) in relevantHighlights"
        :key="highlight.name + index"
        class="flex flex-col"
      >
        <NuxtLink
          class="typo-link-bold link-hover"
          :to="highlight.route"
          @click="searchResultClicked(highlight.route.path, order)"
        >
          <span v-html="highlight.name" />
        </NuxtLink>
        <div
          v-if="highlight.text"
          data-testid="highlighted-field"
          v-html="highlight.text"
        />
      </div>
    </div>
  </div>
</template>
