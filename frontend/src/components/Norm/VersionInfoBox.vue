<script setup lang="ts">
import type { LegislationWork, SearchResult } from "~/types";
import Message from "primevue/message";
import IcBaselineHistory from "~icons/ic/baseline-history";
import {
  getVersionDates,
  getVersionStatus,
  type VersionStatus,
} from "~/composables/useNormVersions";

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

const currentVersion: ComputedRef<SearchResult<LegislationWork>> = computed(
  () => {
    const current = props.versions.find(
      (version) =>
        version.item.workExample.legislationIdentifier ===
        props.currentExpression,
    );
    if (!current)
      throw new Error(
        `The provided current expression (${props.currentExpression}) does not exist in the provided versions`,
      );
    return current;
  },
);

const currentVersionStatus = computed<VersionStatus>(() =>
  getVersionStatus(currentVersion.value),
);

const latestFutureVersion = computed(() => {
  if (currentVersionStatus.value !== "inForce") return undefined;
  const last = props.versions[props.versions.length - 1];
  return getVersionStatus(last) === "future" ? last : undefined;
});

const infoBoxType = computed(() =>
  currentVersionStatus.value === "inForce" ? "info" : "warn",
);
</script>

<template>
  <div v-if="currentVersionStatus" class="mb-40 w-fit">
    <Message :severity="infoBoxType" class="ris-body2-regular">
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
            Neue Fassung ab {{ getVersionDates(latestFutureVersion)[0] }}.
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
