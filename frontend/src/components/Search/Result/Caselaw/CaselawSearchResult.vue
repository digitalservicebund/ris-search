<script setup lang="ts">
import type { CaseLaw, SearchResult, TextMatch } from "@/types";
import { addEllipsis, removeOuterParentheses } from "@/utils/textFormatting";
import GavelIcon from "virtual:icons/material-symbols/gavel";
import { formattedDate } from "@/utils/dateFormatting";
import _ from "lodash";
import { sanitizeSearchResult } from "~/utils/sanitize";

const postHogStore = usePostHogStore();

const props = defineProps<{
  searchResult: SearchResult<CaseLaw>;
  order: number;
}>();

type CaseLawMetadata = {
  headline: string;
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

const metadata = computed(() => {
  const item = props.searchResult.item;
  return {
    headline:
      getMatch("headline", props.searchResult.textMatches) ||
      item.headline ||
      "Titelzeile nicht vorhanden",
    url: `/case-law/${props.searchResult.item.documentNumber}`,
    courtName: item.courtName,
    decisionDate: formattedDate(item.decisionDate),
    fileNumbers: item.fileNumbers?.join(", "),
    decisionName: item.decisionName?.at(0),
    documentType: item.documentType || "Entscheidung",
  } as CaseLawMetadata;
});

const previewSections: ComputedRef<ExtendedTextMatch[]> = computed(() => {
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
  const slice: ExtendedTextMatch[] = [...firstFields, ...otherFields].slice(
    0,
    4,
  );
  if (slice.length === 0) return [];
  const haveHighlight =
    slice.find((field) => field.text.includes("<mark>")) !== undefined;
  // if no fields have a highlight, show only the first one
  if (!haveHighlight) return [slice[0]];
  return slice;
});

function openResult(url: string) {
  postHogStore.searchResultClicked(url, props.order);
}
</script>

<template>
  <div class="my-36 hyphens-auto" data-testid="searchResult">
    <div class="ris-label2-regular flex flex-row flex-wrap items-center gap-8">
      <div class="flex items-center">
        <GavelIcon class="mr-4 h-[1rem] text-gray-900" />
        <span>
          {{ metadata.documentType }}
        </span>
      </div>
      <span>{{ metadata.courtName }}</span>
      <span
        ><span class="sr-only">Entscheidungsdatum </span
        >{{ metadata.decisionDate }}</span
      >
      <span aria-label="Aktenzeichen">{{ metadata.fileNumbers }}</span>
    </div>
    <NuxtLink
      :to="metadata.url"
      class="ris-heading3-bold max-w-title link-hover mt-8 block text-balance text-blue-800"
      @click="openResult(metadata.url)"
    >
      <h2>
        <span v-if="!!metadata.decisionName"
          >{{ metadata.decisionName }} —
        </span>
        <span
          v-html="
            sanitizeSearchResult(removeOuterParentheses(metadata.headline))
          "
        />
      </h2>
    </NuxtLink>
    <div class="mt-6 flex w-full max-w-prose flex-col gap-6">
      <div v-for="section in previewSections" :key="section.id">
        <NuxtLink
          :to="`${metadata.url}#${section.id}`"
          class="ris-label1-bold link-hover text-blue-800"
          @click="openResult(`${metadata.url}#${section.id}`)"
        >
          {{ section.title }}:</NuxtLink
        >{{ " " }}
        <span
          data-testid="highlighted-field"
          class="text-lg"
          v-html="sanitizeSearchResult(section.text)"
        />
      </div>
    </div>
  </div>
</template>
