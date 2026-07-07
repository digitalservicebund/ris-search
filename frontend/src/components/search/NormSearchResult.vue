<script setup lang="ts">
import type { Dayjs } from "dayjs";
import IcBaselineBalance from "~icons/ic/baseline-balance";
import type { RouteLocationRaw, RouteLocationAsPath } from "#vue-router";
import type { SearchResultHeaderItem } from "~/components/search/SearchResultHeader.vue";
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

const resultTypeId = useId();

const headerItems = computed<SearchResultHeaderItem[]>(() => {
  let date: string | Dayjs | undefined =
    searchResult.item.exampleOfWork.legislationDate;

  if (privateFeaturesEnabled) {
    const coverage = temporalCoverageToValidityInterval(
      searchResult.item.temporalCoverage,
    );
    date = coverage?.from;
  }

  return [
    { value: "Norm", id: resultTypeId },
    { value: searchResult.item.abbreviation },
    { value: dateFormattedDDMMYYYY(date) },
  ].filter((i): i is SearchResultHeaderItem => i.value !== undefined);
});

const validityStatus = computed(() =>
  formatNormValidity(searchResult.item.temporalCoverage),
);

const detailPageRoute = computed<RouteLocationAsPath>(() => ({
  path: `/norms/${searchResult.item.legislationIdentifier}`,
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
    <SearchResultHeader :icon="IcBaselineBalance" :items="headerItems">
      <template #trailing>
        <Badge
          v-if="validityStatus"
          class="md:ml-auto"
          v-bind="validityStatus"
        />
      </template>
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
