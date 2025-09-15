<script setup lang="ts">
import Message from "primevue/message";
import type { LegislationWork } from "~/types";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import type { ValidityStatus } from "~/utils/normUtils";
import { temporalCoverageToValidityInterval } from "~/utils/normUtils";
import IcBaselineHistory from "~icons/ic/baseline-history";
import IcBaselineUpdate from "~icons/ic/baseline-update";

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

const toCurrentVersionText = "Zur aktuell gültigen Fassung";
</script>

<template>
  <div
    v-if="showWarningMessage"
    class="mb-40 w-fit"
    data-testid="norm-warning-message"
  >
    <Message :severity="warningMessageType" class="ris-body2-regular">
      <template #icon>
        <IcBaselineUpdate
          v-if="currentVersionValidityStatus === 'InForce'"
          class="text-blue-800"
        />
        <IcBaselineHistory
          v-else-if="currentVersionValidityStatus === 'Expired'"
        />
        <IcBaselineUpdate v-else />
      </template>
      <p>
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
        <span v-else>
          <span class="ris-body2-bold">
            {{
              currentVersionValidityStatus === "Expired"
                ? historicalWarningMessage
                : futureWarningMessage
            }}&nbsp;
          </span>
          <NuxtLink v-if="props.inForceVersionLink" :to="inForceVersionLink">
            {{ toCurrentVersionText }}
          </NuxtLink>
        </span>
      </p>
    </Message>
  </div>
</template>
