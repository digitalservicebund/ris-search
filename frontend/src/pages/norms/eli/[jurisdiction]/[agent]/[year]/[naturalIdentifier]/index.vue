<script setup lang="ts">
import useBackendUrl from "~/composables/useBackendUrl";
import type { JSONLDList, LegislationWork, SearchResult } from "~/types";
import { getMostRelevantExpression } from "~/utils/norm";

const route = useRoute();
const workEli = [
  "eli",
  route.params.jurisdiction,
  route.params.agent,
  route.params.year,
  route.params.naturalIdentifier,
].join("/");

const { data, error: loadError } = await useRisBackend<
  JSONLDList<SearchResult<LegislationWork>>
>(useBackendUrl(`/v1/legislation`), {
  params: {
    eli: workEli,
  },
});

const matchedExpressionEli = computed(() => {
  if (!data.value) return null;
  return getMostRelevantExpression(data.value?.member);
});

if (matchedExpressionEli.value) {
  if (import.meta.client) {
    globalThis.location.href = `/norms/${matchedExpressionEli.value}`;
  } else {
    navigateTo(`/norms/${matchedExpressionEli.value}`, {
      external: true,
      replace: true,
    });
  }
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
