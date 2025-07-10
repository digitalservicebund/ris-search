<script setup lang="ts">
import { useFetch } from "#app";
import { getMostRelevantExpression } from "~/pages/norms/eli/[jurisdiction]/[agent]/[year]/[naturalIdentifier]/[subtype]/index.logic";
import type { JSONLDList, LegislationWork, SearchResult } from "~/types";

const route = useRoute();
const workEli = [
  "eli",
  route.params.jurisdiction,
  route.params.agent,
  route.params.year,
  route.params.naturalIdentifier,
  route.params.subtype,
].join("/");

const backendURL = useBackendURL();
const { data, error: loadError } = await useFetch<
  JSONLDList<SearchResult<LegislationWork>>
>(`${backendURL}/v1/legislation`, {
  params: {
    eli: workEli,
  },
});

const matchedExpressionEli: ComputedRef<string | null> = computed(() => {
  if (!data.value) return null;
  return getMostRelevantExpression(data.value?.member);
});

if (matchedExpressionEli.value) {
  navigateTo(`/norms/${matchedExpressionEli.value}`, { replace: true });
}

if (loadError?.value) {
  showError(loadError.value);
}

if (data.value?.member.length === 0) {
  showError({
    statusCode: 404,
    statusMessage: "no norms found matching work ELI",
  });
}
</script>
<template>
  <DelayedLoadingMessage
    >Suche aktuelle, zukünftige oder historische Fassung…</DelayedLoadingMessage
  >
</template>
