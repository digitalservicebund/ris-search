<script setup lang="ts">
import LegalIcon from "virtual:icons/mdi/legal";
import Badge from "~/components/Badge.vue";
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

const headline = computed(
  () => getMatch("name", highlights.value) || item.value.name,
);

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
</script>

<template>
  <article class="my-36 flex flex-col gap-8 hyphens-auto">
    <p class="ris-label2-regular flex flex-row flex-wrap items-center gap-8">
      <span class="flex items-center">
        <LegalIcon class="mr-4 h-16 text-gray-900" />
        <span>Norm</span>
      </span>
      <span v-if="item.abbreviation">{{ item.abbreviation }}</span>
      <span v-if="formattedDate">{{ formattedDate }}</span>
      <Badge v-if="validityStatus" class="md:ml-auto" v-bind="validityStatus" />
    </p>

    <NuxtLink
      v-if="!!link"
      :to="link"
      class="ris-heading3-bold max-w-title link-hover block text-blue-800"
      @click="openResult(link)"
    >
      <h2
        v-html="
          headline
            ? sanitizeSearchResult(headline)
            : 'Titelzeile nicht vorhanden'
        "
      />
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
            :to="`${link}#${highlight.location || ''}`"
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
  </article>
</template>
