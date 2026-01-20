<script setup lang="ts">
import type { JSONLDList, LegislationExpression } from "~/types";
import { getMostRelevantExpression } from "~/utils/norm";

const route = useRoute();

const { data, error: loadError } = await useRisBackend<
  JSONLDList<LegislationExpression>
>(
  `/v1/legislation/work-example/eli/${route.params.jurisdiction}/${route.params.agent}/${route.params.year}/${route.params.naturalIdentifier}`,
);

const matchedExpressionEli = computed(() => {
  if (!data.value) return null;
  return getMostRelevantExpression(data.value?.member);
});

if (matchedExpressionEli.value) {
  await navigateTo(`/norms/${matchedExpressionEli.value}`, {
    replace: true,
  });
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
