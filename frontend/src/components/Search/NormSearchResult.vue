<script setup lang="ts">
import LegalIcon from "virtual:icons/mdi/legal";
import Badge from "~/components/Badge.vue";
import type { SearchResultHeaderItem } from "~/components/Search/SearchResultHeader.vue";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";
import { usePostHogStore } from "~/stores/usePostHogStore";
import type { LegislationWork, SearchResult, TextMatch } from "~/types";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import { formatNormValidity } from "~/utils/displayValues";
import { temporalCoverageToValidityInterval } from "~/utils/norm";
import { sanitizeSearchResult } from "~/utils/sanitize";
import { addEllipsis } from "~/utils/textFormatting";

const props = defineProps<{
  searchResult: SearchResult<LegislationWork>;
  order: number;
}>();

const postHogStore = usePostHogStore();

const item = computed(() => props.searchResult.item);

const highlights: ComputedRef<TextMatch[]> = computed(
  () => props.searchResult.textMatches,
);

const headline = computed(() => {
  const match =
    getMatch("name", highlights.value) ||
    item.value.name ||
    "Titelzeile nicht vorhanden";

  return sanitizeSearchResult(match);
});

function getMatch(match: string, highlights: TextMatch[]) {
  return highlights.find((highlight) => highlight.name === match)?.text;
}

const link = computed(() => {
  const prefix = "/norms/";
  const expressionEli = item.value.workExample.legislationIdentifier;
  if (!expressionEli) return null;
  return prefix + expressionEli;
});

const privateFeaturesEnabled = usePrivateFeaturesFlag();

const formattedDate = computed(() => {
  const date = privateFeaturesEnabled
    ? temporalCoverageToValidityInterval(
        item.value?.workExample?.temporalCoverage,
      )?.from
    : item.value?.legislationDate;

  return dateFormattedDDMMYYYY(date);
});

const relevantHighlights = computed(() => {
  return highlights.value
    .filter((highlight) => highlight.name != "name")
    .map((hl) => ({ ...hl, text: addEllipsis(hl.text) }));
});

function openResult(url: string) {
  postHogStore.searchResultClicked(url, props.order);
}

const validityStatus = computed(() => {
  return formatNormValidity(item.value.workExample.temporalCoverage);
});

const resultTypeId = useId();

const headerItems = computed<SearchResultHeaderItem[]>(() => {
  return [
    { value: "Norm", id: resultTypeId },
    { value: item.value.abbreviation },
    { value: formattedDate.value },
  ].filter((item): item is SearchResultHeaderItem => item.value !== undefined);
});
</script>

<template>
  <div class="ris-body1-regular my-36 flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader :icon="LegalIcon" :items="headerItems">
      <template #trailing>
        <Badge
          v-if="validityStatus"
          class="md:ml-auto"
          v-bind="validityStatus"
        />
      </template>
    </SearchResultHeader>
    <NuxtLink
      v-if="!!link"
      :to="link"
      :aria-describedby="resultTypeId"
      class="ris-heading3-bold! ris-link1-regular max-w-title link-hover block"
      @click="openResult(link)"
    >
      <h2 v-html="headline" />
    </NuxtLink>

    <div
      class="flex w-full max-w-prose flex-col gap-6"
      data-testid="highlights"
    >
      <div
        v-for="(highlight, index) in relevantHighlights"
        :key="highlight.name + index"
        class="flex flex-col"
      >
        <div class="ris-label1-bold">
          <NuxtLink
            class="link-hover text-blue-800"
            :to="{
              path: `${link}`,
              hash: highlight.location
                ? `#${encodeForUri(highlight.location)}`
                : undefined,
            }"
            @click="openResult(`${link}#${highlight.location || ''}`)"
          >
            <h3 v-html="sanitizeSearchResult(highlight.name)" />
          </NuxtLink>
        </div>
        <div
          v-html="highlight.text ? sanitizeSearchResult(highlight.text) : ''"
        />
      </div>
    </div>
  </div>
</template>
