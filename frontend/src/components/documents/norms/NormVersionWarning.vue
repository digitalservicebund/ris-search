<script setup lang="ts">
import dayjs from "dayjs";
import VersionWarningMessage from "~/components/documents/norms/VersionWarningMessage.vue";
import type { LegislationExpression, LegislationWork } from "~/types";
import {
  getValidityStatus,
  temporalCoverageToValidityInterval,
} from "~/utils/norm";

const props = defineProps<{
  versions: LegislationExpression[];
  currentVersion: LegislationWork;
}>();

const inForceVersion = computed(() =>
  props.versions.find((version) => version.legislationLegalForce === "InForce"),
);

const inForceVersionLink = computed(() =>
  inForceVersion.value
    ? `/norms/${inForceVersion.value?.legislationIdentifier}`
    : undefined,
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
    const from1 = temporalCoverageToValidityInterval(v1.temporalCoverage)?.from;
    const from2 = temporalCoverageToValidityInterval(v2.temporalCoverage)?.from;
    return dayjs(from1).diff(from2, "day");
  });

  const firstFutureInForce = sorted.find((v) => {
    return (
      getValidityStatus(
        temporalCoverageToValidityInterval(v.temporalCoverage),
      ) === "FutureInForce"
    );
  });

  return firstFutureInForce;
});
</script>

<template>
  <VersionWarningMessage
    :current-version-validity-status="currentVersionValidityStatus"
    :in-force-version-link="inForceVersionLink"
    :future-version="firstFutureVersion"
    historical-warning-message="Sie lesen eine historische Fassung."
    future-warning-message="Sie lesen eine zukünftige Fassung."
  />
</template>
