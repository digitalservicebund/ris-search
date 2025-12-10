<script setup lang="ts">
import _ from "lodash";
import type { RouteLocationRaw } from "#vue-router";
import type { SearchResultHeaderItem } from "~/components/Search/SearchResultHeader.vue";
import { usePostHogStore } from "~/stores/usePostHogStore";
import type { CaseLaw, SearchResult, TextMatch } from "~/types";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import { sanitizeSearchResult } from "~/utils/sanitize";
import { addEllipsis, removeOuterParentheses } from "~/utils/textFormatting";
import GavelIcon from "~icons/material-symbols/gavel";

const postHogStore = usePostHogStore();

const props = defineProps<{
  searchResult: SearchResult<CaseLaw>;
  order: number;
}>();

type CaseLawMetadata = {
  headline: string;
  route: RouteLocationRaw;
  url: string;
  decisionName: string;
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
    decisionName: item.decisionName?.at(0),
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

const resultTypeId = useId();

const headerItems = computed(() => {
  const item = props.searchResult.item;

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

const headline = computed(() =>
  sanitizeSearchResult(removeOuterParentheses(metadata.value.headline)),
);

function trackResultClick(url: string) {
  postHogStore.searchResultClicked(url, props.order);
}
</script>

<template>
  <div class="ris-body1-regular my-36 flex flex-col gap-8 hyphens-auto">
    <SearchResultHeader :icon="GavelIcon" :items="headerItems" />
    <NuxtLink
      :to="metadata.route"
      :aria-describedby="resultTypeId"
      class="ris-heading3-bold! ris-link1-regular max-w-title link-hover block"
      @click="trackResultClick(metadata.url)"
    >
      <h2>
        <span v-if="!!metadata.decisionName">
          {{ metadata.decisionName }} —
        </span>
        <span v-html="headline" />
      </h2>
    </NuxtLink>

    <div class="flex w-full max-w-prose flex-col gap-6">
      <div v-for="section in previewSections" :key="section?.id">
        <NuxtLink
          :to="{ path: `${metadata.url}`, hash: `#${section?.id}` }"
          class="ris-link1-bold link-hover"
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
  </div>
</template>
