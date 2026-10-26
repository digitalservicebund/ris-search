<script setup lang="ts">
const {
  currentVersionValidityStatus,
  documentTerm,
  futureVersion,
  inForceVersionLink,
} = defineProps<{
  currentVersionValidityStatus?: ValidityStatus;
  /** Noun used in the message */
  documentTerm: "Fassung" | "Gesamtausgabe";
  futureVersion?: FutureVersionTarget;
  inForceVersionLink?: RouteLocationRaw;
}>();

const hasFutureVersion = computed(
  () => currentVersionValidityStatus === "InForce" && !!futureVersion,
);

const showWarningMessage = computed(
  () =>
    hasFutureVersion.value ||
    currentVersionValidityStatus === "Expired" ||
    currentVersionValidityStatus === "FutureInForce",
);

const versionTextId = useId();

const versionText = computed(() => {
  if (hasFutureVersion.value) {
    const formattedFutureDate = dateFormattedDDMMYYYY(futureVersion?.validFrom);
    return `Ab ${formattedFutureDate} gilt eine neue ${documentTerm}.`;
  }

  if (currentVersionValidityStatus === "Expired") {
    return `Sie lesen eine historische ${documentTerm}.`;
  }

  return `Sie lesen eine zukünftige ${documentTerm}.`;
});

const versionLink = computed<
  { to: RouteLocationRaw; label: string } | undefined
>(() => {
  if (futureVersion && hasFutureVersion.value) {
    return { to: futureVersion.to, label: `Zur zukünftigen ${documentTerm}` };
  }

  if (inForceVersionLink) {
    return {
      to: inForceVersionLink,
      label: `Zur aktuell gültigen ${documentTerm}`,
    };
  }

  return undefined;
});
</script>

<script lang="ts">
import type { Dayjs } from "dayjs";
import type { RouteLocationRaw } from "#vue-router";

export type FutureVersionTarget = {
  to: RouteLocationRaw;
  validFrom?: Dayjs;
};
</script>

<template>
  <div v-if="showWarningMessage" class="w-fit">
    <UiMessage hide-icon>
      <p>
        <span :id="versionTextId">{{ versionText }}</span
        >{{ " " }}

        <NuxtLink
          v-if="versionLink"
          :to="versionLink.to"
          class="typo-link2-regular"
          :aria-describedby="versionTextId"
        >
          {{ versionLink.label }}
        </NuxtLink>
      </p>
    </UiMessage>
  </div>
</template>
