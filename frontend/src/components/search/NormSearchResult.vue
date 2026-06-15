<script setup lang="ts">
import type { Dayjs } from "dayjs";
import IcBaselineBalance from "~icons/ic/baseline-balance";
import type { SearchResultHeaderItem } from "~/components/search/SearchResultHeader.vue";
import type { LegislationExpression, SearchResult } from "~/types/api";
import { getMatch, getTitleWithFallback } from "~/utils/search/searchResults";

const { searchResult, order } = defineProps<{
  searchResult: SearchResult<LegislationExpression>;
  order: number;
}>();

const { searchResultClicked } = usePostHog();

const privateFeaturesEnabled = usePrivateFeaturesFlag();

const headline = computed(() =>
  getTitleWithFallback(
    getMatch("name", searchResult.textMatches),
    searchResult.item.name,
  ),
);

const resultTypeId = useId();

const headerItems = computed<SearchResultHeaderItem[]>(() => {
  let date: string | Dayjs | undefined =
    searchResult.item?.exampleOfWork.legislationDate;

  if (privateFeaturesEnabled) {
    const coverage = temporalCoverageToValidityInterval(
      searchResult.item?.temporalCoverage,
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

const detailPageUrl = computed(() => {
  const prefix = "/norms/";
  const expressionEli = searchResult.item.legislationIdentifier;
  if (!expressionEli) return null;
  return prefix + expressionEli;
});

const relevantHighlights = computed(() =>
  searchResult.textMatches
    .filter((highlight) => highlight.name != "name")
    .map((hl) => ({
      name: sanitizeSearchResult(hl.name),
      text: sanitizeSearchResult(addEllipsis(hl.text) ?? ""),
      location: hl.location,
    })),
);

function getArticleLink(highlight: { location?: string | null }) {
  return `${detailPageUrl.value}/${highlight.location ?? ""}`;
}
</script>

<template>
  <div class="my-36 flex flex-col gap-8 hyphens-auto">
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
      v-if="!!detailPageUrl"
      :to="detailPageUrl"
      :aria-describedby="resultTypeId"
      class="ris-heading3-bold! ris-link1-regular link-hover block"
      @click="searchResultClicked(detailPageUrl, order)"
    >
      <h2 v-html="headline" />
    </NuxtLink>

    <div class="flex w-full flex-col gap-6" data-testid="highlights">
      <div
        v-for="(highlight, index) in relevantHighlights"
        :key="highlight.name + index"
        class="flex flex-col"
      >
        <NuxtLink
          class="ris-link1-bold link-hover"
          :to="getArticleLink(highlight)"
          @click="searchResultClicked(getArticleLink(highlight), order)"
        >
          <span v-html="highlight.name" />
        </NuxtLink>
        <div v-html="highlight.text" />
      </div>
    </div>
  </div>
</template>
