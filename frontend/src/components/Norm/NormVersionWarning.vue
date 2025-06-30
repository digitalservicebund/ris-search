<script setup lang="ts">
import type { LegislationWork, SearchResult } from "~/types";
import {
  getVersionStatus,
  type VersionStatus,
} from "~/composables/useNormVersions";
import VersionWarningMessage from "~/components/Norm/VersionWarningMessage.vue";

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

const currentVersionStatus = computed<VersionStatus>(() =>
  getVersionStatus(props.currentVersion),
);

const latestFutureVersion = computed(() => {
  if (currentVersionStatus.value !== "inForce") return undefined;
  const last = props.versions[props.versions.length - 1];
  return getVersionStatus(last.item) === "future" ? last.item : undefined;
});
</script>

<template>
  <VersionWarningMessage
    :current-version-status="currentVersionStatus"
    :in-force-version-link="inForceVersionLink"
    :future-version="latestFutureVersion"
    historical-warning-message="Historische Fassung."
    future-warning-message="Zukünftige Fassung."
  />
</template>
