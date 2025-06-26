<script setup lang="ts">
import type { LegislationWork } from "~/types";
import Message from "primevue/message";
import IcBaselineHistory from "~icons/ic/baseline-history";
import {
  getVersionDates,
  type VersionStatus,
} from "~/composables/useNormVersions";

export interface WarningMessageProps {
  currentVersionStatus: VersionStatus;
  inForceVersionLink: string;
  futureVersion?: LegislationWork;
  historicalWarningMessage: string;
  futureWarningMessage: string;
}

const props = defineProps<{
  currentVersionStatus: VersionStatus;
  inForceVersionLink: string;
  futureVersion?: LegislationWork;
  historicalWarningMessage: string;
  futureWarningMessage: string;
}>();

const warningMessageType = computed(() =>
  props.currentVersionStatus === "inForce" ? "info" : "warn",
);

const showWarningMessage = computed(() => {
  return (
    (props.currentVersionStatus === "inForce" && props.futureVersion) ||
    props.currentVersionStatus === "historical" ||
    props.currentVersionStatus === "future"
  );
});
</script>

<template>
  <div
    v-if="showWarningMessage"
    class="mb-40 w-fit"
    data-testid="norm-warning-message"
  >
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
        <span v-if="currentVersionStatus === 'inForce' && props.futureVersion">
          <span class="ris-body2-bold">
            Neue Fassung ab {{ getVersionDates(props.futureVersion)[0] }}.
          </span>
          <NuxtLink
            :to="`/norms/${props.futureVersion.workExample.legislationIdentifier}`"
          >
            Zur zukünftigen Fassung
          </NuxtLink>
        </span>
        <span v-else-if="currentVersionStatus === 'historical'">
          <span class="ris-body2-bold">
            {{ historicalWarningMessage }}
          </span>
          <NuxtLink v-if="props.inForceVersionLink" :to="inForceVersionLink">
            Zur aktuell gültigen Fassung
          </NuxtLink>
        </span>
        <span v-else-if="currentVersionStatus === 'future'">
          <span class="ris-body2-bold">{{ futureWarningMessage }} </span>
          <NuxtLink v-if="props.inForceVersionLink" :to="inForceVersionLink">
            Zur aktuell gültigen Fassung
          </NuxtLink>
        </span>
      </p>
    </Message>
  </div>
</template>
