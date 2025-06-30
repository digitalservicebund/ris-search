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
  props.currentVersionValidityStatus === ValidityStatus.InForce
    ? "info"
    : "warn",
);

const showWarningMessage = computed(() => {
  return (
    (props.currentVersionValidityStatus === ValidityStatus.InForce &&
      props.futureVersion) ||
    props.currentVersionValidityStatus === ValidityStatus.Historical ||
    props.currentVersionValidityStatus === ValidityStatus.Future
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
          v-if="currentVersionValidityStatus === ValidityStatus.InForce"
          class="text-blue-800"
        />
        <IcBaselineHistory
          v-else-if="currentVersionValidityStatus === ValidityStatus.Historical"
          class="scale-x-[-1]"
        />
        <IcBaselineHistory v-else />
      </template>
      <p class="mt-2">
        <span
          v-if="
            currentVersionValidityStatus === ValidityStatus.InForce &&
            props.futureVersion
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
        <span
          v-else-if="currentVersionValidityStatus === ValidityStatus.Historical"
        >
          <span class="ris-body2-bold">
            {{ historicalWarningMessage }}
          </span>
          <NuxtLink v-if="props.inForceVersionLink" :to="inForceVersionLink">
            Zur aktuell gültigen Fassung
          </NuxtLink>
        </span>
        <span
          v-else-if="currentVersionValidityStatus === ValidityStatus.Future"
        >
          <span class="ris-body2-bold">{{ futureWarningMessage }} </span>
          <NuxtLink v-if="props.inForceVersionLink" :to="inForceVersionLink">
            Zur aktuell gültigen Fassung
          </NuxtLink>
        </span>
      </p>
    </Message>
  </div>
</template>
