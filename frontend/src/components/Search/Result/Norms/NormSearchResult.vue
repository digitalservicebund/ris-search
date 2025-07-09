<script setup lang="ts">
import LegalIcon from "virtual:icons/mdi/legal";
import type { LegislationWork, SearchResult, TextMatch } from "~/types";
import { formattedDate } from "~/utils/dateFormatting";

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
  const prefix = "norms/";
  const expressionEli = item.value.workExample.legislationIdentifier;
  if (!expressionEli) return null;
  return prefix + expressionEli;
});

const relevantHighlights = computed(() => {
  return highlights.value
    .filter((highlight) => highlight.name != "name")
    .map((hl) => ({ ...hl, text: addEllipsis(hl.text) }));
});
function openResult(url: string) {
  postHogStore.searchResultClicked(url, props.order);
}
</script>

<template>
  <div class="my-36 hyphens-auto" data-testid="searchResult">
    <div class="ris-label2-regular flex flex-row flex-wrap items-center gap-8">
      <div class="flex items-center">
        <LegalIcon class="mr-4 h-[1rem] text-gray-900" />
        <span>Norm</span>
      </div>
      <span v-if="item.abbreviation">{{ item.abbreviation }}</span>
      <span>{{ formattedDate(item.legislationDate) }}</span>
    </div>
    <NuxtLink
      v-if="!!link"
      :to="link"
      class="ris-heading3-bold max-w-title link-hover mt-8 block text-balance text-blue-800"
      @click="openResult(link)"
    >
      <div
        v-html="
          headline
            ? sanitizeSearchResult(headline)
            : 'Titelzeile nicht vorhanden'
        "
      />
    </NuxtLink>

    <div
      class="mt-6 flex w-full max-w-prose flex-col gap-6"
      data-testid="highlights"
    >
      <div
        v-for="(highlight, index) in relevantHighlights"
        :key="highlight.name + index"
        class="flex flex-col"
      >
        <div class="ris-label1-bold mt-8">
          <NuxtLink
            class="link-hover text-blue-800"
            :to="`${link}#${highlight.location || ''}`"
            @click="openResult(`${link}#${highlight.location || ''}`)"
          >
            <span v-html="sanitizeSearchResult(highlight.name)" />
          </NuxtLink>
        </div>
        <div
          v-html="highlight.text ? sanitizeSearchResult(highlight.text) : ''"
        />
      </div>
    </div>
  </div>
</template>
