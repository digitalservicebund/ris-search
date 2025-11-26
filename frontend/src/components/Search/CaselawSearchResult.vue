<script setup lang="ts">
import _ from "lodash";
import GavelIcon from "virtual:icons/material-symbols/gavel";
import type { RouteLocationRaw } from "#vue-router";
import { usePostHogStore } from "~/stores/usePostHogStore";
import type { CaseLaw, SearchResult, TextMatch } from "~/types";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import { sanitizeSearchResult } from "~/utils/sanitize";
import { addEllipsis, removeOuterParentheses } from "~/utils/textFormatting";

const postHogStore = usePostHogStore();

const props = defineProps<{
  searchResult: SearchResult<CaseLaw>;
  order: number;
}>();

type CaseLawMetadata = {
  headline: string;
  route: RouteLocationRaw;
  url: string;
  courtName: string;
  decisionDate: string;
  fileNumbers: string;
  decisionName: string;
  documentType: string;
};

function getMatch(match: string, matches: TextMatch[]) {
  return matches.find((highlight) => highlight.name === match)?.text;
}

function getMatches(match: string, matches: TextMatch[]) {
  return matches
    .filter((highlight) => highlight.name === match)
    .map((highlight) => highlight.text);
}

type Key =
  | "guidingPrinciple"
  | "headnote"
  | "otherHeadnote"
  | "tenor"
  | "grounds"
  | "caseFacts"
  | "decisionGrounds";

interface FieldDisplayProperties {
  id: string;
  title: string;
}

type ExtendedTextMatch = TextMatch & FieldDisplayProperties;

// field definitions. A Map is used to preserve order, with the first present item
const fields: Map<Key, FieldDisplayProperties> = new Map([
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

function getFileNumbers(item: CaseLaw) {
  const matches = getMatches("fileNumbers", props.searchResult.textMatches);
  if (matches.length) {
    const replaced = [...item.fileNumbers];
    for (const match of matches) {
      const stripped = sanitizeSearchResult(match, []);
      const index = item.fileNumbers.indexOf(stripped);
      if (index !== -1) {
        replaced[index] = match;
      }
    }
    return replaced.join(", ");
  }
  return item.fileNumbers?.join(", ");
}

const metadata = computed(() => {
  const item = props.searchResult.item;
  return {
    headline:
      getMatch("headline", props.searchResult.textMatches) ||
      item.headline ||
      "Titelzeile nicht vorhanden",
    route: {
      name: "case-law-documentNumber",
      params: { documentNumber: props.searchResult.item.documentNumber },
    },
    // The URL is currently needed for PostHog tracking but should not be used
    // for navigation. Use `route` for navigation instead.
    url: `/case-law/${props.searchResult.item.documentNumber}`,
    courtName: item.courtName,
    decisionDate: dateFormattedDDMMYYYY(item.decisionDate),
    fileNumbers: getFileNumbers(item),
    decisionName: item.decisionName?.at(0),
    documentType: item.documentType || "Entscheidung",
  } as CaseLawMetadata;
});

const previewSections = computed<ExtendedTextMatch[]>(() => {
  const textMatches = props.searchResult.textMatches;
  const foundFields = new Set<Key>();
  const relevantMatches = textMatches
    .filter((match) => fields.has(match.name as Key))
    .map((match) => {
      foundFields.add(match.name as Key);
      return {
        ...match,
        text: addEllipsis(match.text),
        ...fields.get(match.name as Key),
      } as ExtendedTextMatch;
    });

  // always show the most relevant field, regardless of highlight status
  const firstFieldName = [...fields.keys()].find((key) => foundFields.has(key));
  const [firstFields, otherFields] = _.partition(
    relevantMatches,
    (match) => match.name === firstFieldName,
  );

  // show up to 4 fields
  const slice: ExtendedTextMatch[] = [...firstFields, ...otherFields]
    .slice(0, 4)
    .filter((i) => !!i);

  if (slice.length === 0) return [];

  const haveHighlight = slice.some((field) => field.text.includes("<mark>"));

  // if no fields have a highlight, show only the first one
  // casting because TypeScript doesn't realize we already ensured it's not undefined
  if (!haveHighlight) return [slice[0] as ExtendedTextMatch];

  return slice;
});

function trackResultClick(url: string) {
  postHogStore.searchResultClicked(url, props.order);
}

const headingId = useId();
</script>

<template>
  <article
    class="my-36 flex flex-col gap-8 hyphens-auto"
    :aria-labelledby="headingId"
  >
    <p class="ris-label2-regular flex flex-row flex-wrap items-center gap-8">
      <span class="flex items-center">
        <GavelIcon class="mr-4 h-16 text-gray-900" />
        <span>{{ metadata.documentType }}</span>
      </span>
      <span>{{ metadata.courtName }}</span>
      <span>{{ metadata.decisionDate }}</span>
      <span v-html="metadata.fileNumbers" />
    </p>

    <NuxtLink
      :to="metadata.url"
      class="ris-heading3-bold max-w-title link-hover block text-blue-800"
      @click="trackResultClick(metadata.url)"
    >
      <h2 :id="headingId">
        <span v-if="!!metadata.decisionName">
          {{ metadata.decisionName }} —
        </span>
        <span
          v-html="
            sanitizeSearchResult(removeOuterParentheses(metadata.headline))
          "
        />
      </h2>
    </NuxtLink>

    <div class="flex w-full max-w-prose flex-col gap-6">
      <div v-for="section in previewSections" :key="section?.id">
        <NuxtLink
          :to="{ path: `${metadata.url}`, hash: `#${section?.id}` }"
          class="ris-label1-bold link-hover text-blue-800"
          external
          @click="trackResultClick(`${metadata.url}#${section?.id}`)"
          >{{ section?.title }}:</NuxtLink
        >{{ " " }}
        <span
          v-if="section.text"
          data-testid="highlighted-field"
          class="ris-label1-regular"
          v-html="sanitizeSearchResult(section.text)"
        />
      </div>
    </div>
  </article>
</template>
