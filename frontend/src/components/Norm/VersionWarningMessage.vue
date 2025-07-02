<script setup lang="ts">
import type { LegislationWork } from "~/types";
import Message from "primevue/message";
import IcBaselineHistory from "~icons/ic/baseline-history";

export interface VersionWarningMessageProps {
  currentVersionValidityStatus?: ValidityStatus;
  inForceVersionLink: string;
  futureVersion?: LegislationWork;
  historicalWarningMessage: string;
  futureWarningMessage: string;
}

const props = defineProps<VersionWarningMessageProps>();

const warningMessageType = computed(() =>
  props.currentVersionValidityStatus === "InForce" ? "info" : "warn",
);

const showWarningMessage = computed(() => {
  return (
    (props.currentVersionValidityStatus === "InForce" && props.futureVersion) ||
    props.currentVersionValidityStatus === "Expired" ||
    props.currentVersionValidityStatus === "FutureInForce"
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
          v-if="currentVersionValidityStatus === 'InForce'"
          class="text-blue-800"
        />
        <IcBaselineHistory
          v-else-if="currentVersionValidityStatus === 'Expired'"
          class="scale-x-[-1]"
        />
        <IcBaselineHistory v-else />
      </template>
      <p class="mt-2">
        <span
          v-if="
            currentVersionValidityStatus === 'InForce' && props.futureVersion
          "
        >
          <span class="ris-body2-bold">
            Neue Fassung ab
            {{
              dateFormattedDDMMYYYY(
                temporalCoverageToValidityInterval(
                  props.futureVersion.workExample.temporalCoverage,
                )?.from,
              )
            }}.
          </span>
          <NuxtLink
            :to="`/norms/${props.futureVersion.workExample.legislationIdentifier}`"
          >
            Zur zukünftigen Fassung
          </NuxtLink>
        </span>
        <span v-else-if="currentVersionValidityStatus === 'Expired'">
          <span class="ris-body2-bold">
            {{ historicalWarningMessage }}
          </span>
          <NuxtLink v-if="props.inForceVersionLink" :to="inForceVersionLink">
            Zur aktuell gültigen Fassung
          </NuxtLink>
        </span>
        <span v-else-if="currentVersionValidityStatus === 'FutureInForce'">
          <span class="ris-body2-bold">{{ futureWarningMessage }} </span>
          <NuxtLink v-if="props.inForceVersionLink" :to="inForceVersionLink">
            Zur aktuell gültigen Fassung
          </NuxtLink>
        </span>
      </p>
    </Message>
  </div>
</template>
