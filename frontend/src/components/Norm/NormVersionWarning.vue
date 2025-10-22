<script setup lang="ts">
import dayjs from "dayjs";
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

const firstFutureVersion = computed(() => {
  if (currentVersionValidityStatus.value !== "InForce") return undefined;

  const sorted = props.versions.toSorted((v1, v2) => {
    const from1 = temporalCoverageToValidityInterval(
      v1.item.workExample.temporalCoverage,
    )?.from;
    const from2 = temporalCoverageToValidityInterval(
      v2.item.workExample.temporalCoverage,
    )?.from;
    return dayjs(from1).diff(from2, "day");
  });

  const firstFutureInForce = sorted.find((v) => {
    return (
      getValidityStatus(
        temporalCoverageToValidityInterval(v.item.workExample.temporalCoverage),
      ) === "FutureInForce"
    );
  });

  return firstFutureInForce?.item;
});
</script>

<template>
  <VersionWarningMessage
    :current-version-validity-status="currentVersionValidityStatus"
    :in-force-version-link="inForceVersionLink"
    :future-version="firstFutureVersion"
    historical-warning-message="Historische Fassung."
    future-warning-message="Zukünftige Fassung."
  />
</template>
