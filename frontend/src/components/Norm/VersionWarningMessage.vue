<script setup lang="ts">
import Message from "primevue/message";
import type { RouteLocationRaw } from "#vue-router";
import type { LegislationWork } from "~/types";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import type { ValidityStatus } from "~/utils/norm";
import { temporalCoverageToValidityInterval } from "~/utils/norm";
import IcBaselineHistory from "~icons/ic/baseline-history";
import IcBaselineUpdate from "~icons/ic/baseline-update";

const {
  currentVersionValidityStatus,
  futureVersion,
  futureWarningMessage,
  historicalWarningMessage,
  inForceVersionLink,
} = defineProps<{
  currentVersionValidityStatus?: ValidityStatus;
  futureVersion?: LegislationWork;
  futureWarningMessage: string;
  historicalWarningMessage: string;
  inForceVersionLink?: string;
}>();

const warningMessageType = computed(() =>
  currentVersionValidityStatus === "InForce" ? "info" : "warn",
);

const showWarningMessage = computed(
  () =>
    (currentVersionValidityStatus === "InForce" && futureVersion) ||
    currentVersionValidityStatus === "Expired" ||
    currentVersionValidityStatus === "FutureInForce",
);

const versionTextId = useId();

const versionText = computed(() => {
  if (currentVersionValidityStatus === "InForce" && futureVersion) {
    const formattedFutureDate = dateFormattedDDMMYYYY(
      temporalCoverageToValidityInterval(
        futureVersion.workExample.temporalCoverage,
      )?.from,
    );
    return `Ab ${formattedFutureDate} gilt eine neue Fassung.`;
  } else {
    return currentVersionValidityStatus === "Expired"
      ? historicalWarningMessage
      : futureWarningMessage;
  }
});

const versionLink = computed<
  { to: RouteLocationRaw; label: string } | undefined
>(() => {
  if (currentVersionValidityStatus === "InForce" && futureVersion) {
    return {
      to: `/norms/${futureVersion.workExample.legislationIdentifier}`,
      label: "Zur zukünftigen Fassung",
    };
  } else if (inForceVersionLink) {
    return { to: inForceVersionLink, label: "Zur aktuell gültigen Fassung" };
  } else {
    return undefined;
  }
});
</script>

<template>
  <div v-if="showWarningMessage" class="mb-40 w-fit">
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
        <span :id="versionTextId" class="ris-label2-bold">{{
          versionText
        }}</span
        >{{ " " }}

        <NuxtLink
          v-if="versionLink"
          :to="versionLink.to"
          class="ris-link2-regular"
          :aria-describedby="versionTextId"
        >
          {{ versionLink.label }}
        </NuxtLink>
      </p>
    </Message>
  </div>
</template>
