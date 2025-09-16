<script setup lang="ts">
import VersionWarningMessage from "~/components/Norm/VersionWarningMessage.vue";
import type { LegislationWork, SearchResult } from "~/types";
import {
  getValidityStatus,
  temporalCoverageToValidityInterval,
} from "~/utils/normUtils";

const props = defineProps<{
  versions: SearchResult<LegislationWork>[];
  currentVersion: LegislationWork;
}>();

const inForceVersion = computed(() =>
  props.versions.find(
    (version) => version.item.workExample.legislationLegalForce === "InForce",
  ),
);

const inForceVersionLink = computed(
  () =>
    `/norms/${inForceVersion.value?.item.workExample.legislationIdentifier}`,
);

const currentVersionValidityStatus = computed(() => {
  const validityInterval = temporalCoverageToValidityInterval(
    props.currentVersion.workExample.temporalCoverage,
  );
  return getValidityStatus(validityInterval);
});

const latestFutureVersion = computed(() => {
  if (currentVersionValidityStatus.value !== "InForce") return undefined;
  const last = props.versions[props.versions.length - 1];
  const latestValidityInterval = temporalCoverageToValidityInterval(
    last.item.workExample.temporalCoverage,
  );
  return getValidityStatus(latestValidityInterval) === "FutureInForce"
    ? last.item
    : undefined;
});
</script>

<template>
  <VersionWarningMessage
    :current-version-validity-status="currentVersionValidityStatus"
    :in-force-version-link="inForceVersionLink"
    :future-version="latestFutureVersion"
    historical-warning-message="Historische Fassung."
    future-warning-message="Zukünftige Fassung."
  />
</template>
