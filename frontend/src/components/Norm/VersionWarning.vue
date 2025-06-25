<script setup lang="ts">
import type { LegislationWork, SearchResult } from "~/types";
import {
  getVersionStatus,
  type VersionStatus,
} from "~/composables/useNormVersions";
import WarningMessage from "~/components/Norm/WarningMessage.vue";

const props = defineProps<{
  versions: SearchResult<LegislationWork>[];
  currentExpression: string;
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

const currentVersion: ComputedRef<SearchResult<LegislationWork> | undefined> =
  computed(() => {
    const current = props.versions.find(
      (version) =>
        version.item.workExample.legislationIdentifier ===
        props.currentExpression,
    );
    if (!current)
      showError({
        statusMessage: `The provided current expression (${props.currentExpression}) does not exist in the provided versions`,
      });
    return current;
  });

const currentVersionStatus = computed<VersionStatus>(() =>
  getVersionStatus(currentVersion.value),
);

const latestFutureVersion = computed(() => {
  if (currentVersionStatus.value !== "inForce") return undefined;
  const last = props.versions[props.versions.length - 1];
  return getVersionStatus(last) === "future" ? last : undefined;
});
</script>

<template>
  <WarningMessage
    :current-version-status="currentVersionStatus"
    :in-force-version-link="inForceVersionLink"
    :future-version="latestFutureVersion"
    historical-warning-message="Historische Fassung."
    future-warning-message="Zukünftige Fassung."
  />
</template>
