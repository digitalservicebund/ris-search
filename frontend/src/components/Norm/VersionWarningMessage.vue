<script setup lang="ts">
import type { LegislationWork, SearchResult } from "~/types";
import Message from "primevue/message";
import IcBaselineHistory from "~icons/ic/baseline-history";
import {
  getVersionStatus,
  type VersionStatus,
} from "~/composables/useNormVersions";
import { temporalCoverageToValidityInterval } from "~/utils/normUtils";

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

const warningMessageType = computed(() =>
  currentVersionStatus.value === "inForce" ? "info" : "warn",
);

const showWarningMessage = computed(() => {
  return (
    (currentVersionStatus.value === "inForce" && latestFutureVersion.value) ||
    currentVersionStatus.value === "historical" ||
    currentVersionStatus.value === "future"
  );
});
</script>

<template>
  <div v-if="showWarningMessage" class="mb-40 w-fit">
    <Message :severity="warningMessageType" class="ris-body2-regular">
      <template #icon>
        <IcBaselineHistory
          v-if="currentVersionStatus === 'inForce'"
          class="text-blue-800"
        />
        <IcBaselineHistory
          v-else-if="currentVersionStatus === 'historical'"
          class="scale-x-[-1]"
        />
        <IcBaselineHistory v-else />
      </template>
      <p class="mt-2">
        <span v-if="currentVersionStatus === 'inForce' && latestFutureVersion">
          <span class="ris-body2-bold">
            Neue Fassung ab
            {{
              temporalCoverageToValidityInterval(
                latestFutureVersion?.item.workExample.temporalCoverage,
              )?.from
            }}.
          </span>
          <NuxtLink
            :to="`/norms/${latestFutureVersion.item.workExample.legislationIdentifier}`"
          >
            Zur zukünftigen Fassung
          </NuxtLink>
        </span>
        <span v-else-if="currentVersionStatus === 'historical'">
          <span class="ris-body2-bold"> Historische Fassung. </span>
          <NuxtLink v-if="inForceVersion" :to="inForceVersionLink">
            Zur aktuell gültigen Fassung
          </NuxtLink>
        </span>
        <span v-else-if="currentVersionStatus === 'future'">
          <span class="ris-body2-bold">Zukünftige Fassung. </span>
          <NuxtLink v-if="inForceVersion" :to="inForceVersionLink">
            Zur aktuell gültigen Fassung
          </NuxtLink>
        </span>
      </p>
    </Message>
  </div>
</template>
