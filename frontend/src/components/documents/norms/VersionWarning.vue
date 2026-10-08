<script setup lang="ts">
import type { RouteLocationRaw } from "#vue-router";
import type { LegislationExpression } from "~/types/api";
import type { FutureVersionTarget } from "./VersionWarningMessage.vue";

const props = defineProps<{
  versions: LegislationExpression[];
  currentVersion: LegislationExpression;
}>();

const route = useRoute();

function getVersionRoute(expressionEli: string): RouteLocationRaw {
  return {
    path: `/gesetze/${expressionEli}`,
    query: { from: route.query.from },
  };
}

const inForceVersionLink = computed<RouteLocationRaw | undefined>(() => {
  const inForceVersion = props.versions.find(
    (version) => version.legislationLegalForce === "InForce",
  );
  if (!inForceVersion) return undefined;

  return getVersionRoute(inForceVersion.legislationIdentifier);
});

const currentVersionValidityStatus = computed(() =>
  getVersionValidityStatus(props.currentVersion),
);

const futureVersion = computed<FutureVersionTarget | undefined>(() => {
  if (currentVersionValidityStatus.value !== "InForce") return undefined;

  const nextFutureVersion = findNextFutureVersion(props.versions);
  if (!nextFutureVersion) return undefined;

  return {
    to: getVersionRoute(nextFutureVersion.legislationIdentifier),
    validFrom: getVersionValidFrom(nextFutureVersion),
  };
});
</script>

<template>
  <DocumentsNormsVersionWarningMessage
    :current-version-validity-status="currentVersionValidityStatus"
    :in-force-version-link="inForceVersionLink"
    :future-version="futureVersion"
    document-term="Gesamtausgabe"
  />
</template>
