<script setup lang="ts">
import type { LegislationWork, SearchResult } from "~/types";
import { splitTemporalCoverage } from "~/utils/dateFormatting";
import Message from "primevue/message";
import IcBaselineHistory from "~icons/ic/baseline-history";

const props = defineProps<{
  versions: SearchResult<LegislationWork>[];
  currentExpression: string;
}>();

type Status = "inForce" | "future" | "historical" | undefined;

const inForceVersion = computed(() => {
  return props.versions.find(
    (version) => version.item.workExample.legislationLegalForce === "InForce",
  );
});

const inForceVersionStartDate = computed(() => {
  if (!inForceVersion.value) return undefined;
  return splitTemporalCoverage(
    inForceVersion.value.item.workExample.temporalCoverage || "",
  )[0];
});

const currentVersion: ComputedRef<SearchResult<LegislationWork>> = computed(
  () => {
    const current = props.versions.find(
      (version) =>
        version.item.workExample.legislationIdentifier ===
        props.currentExpression,
    );
    if (!current)
      throw new Error(
        `Current version not found for prop expression: ${props.currentExpression}`,
      );
    return current;
  },
);

const currentVersionStartDate = computed(() => {
  return splitTemporalCoverage(
    currentVersion.value.item.workExample.temporalCoverage || "",
  )[0];
});

const status = computed<Status>(() => {
  if (
    props.currentExpression ===
    inForceVersion.value?.item.workExample.legislationIdentifier
  )
    return "inForce";
  if (!currentVersionStartDate.value || !inForceVersionStartDate.value)
    return undefined;
  return currentVersionStartDate.value > inForceVersionStartDate.value
    ? "future"
    : "historical";
});

const latestFutureVersion = computed(() => {
  if (status.value !== "inForce") return undefined;
  const last = props.versions[props.versions.length - 1];
  if (
    last.item.workExample.legislationLegalForce !== "InForce" &&
    last.item.workExample.legislationIdentifier !==
      inForceVersion.value?.item.workExample.legislationIdentifier
  ) {
    return last;
  }
  return undefined;
});

const infoBoxProperties = computed(() => {
  if (status.value === "inForce" && latestFutureVersion.value) {
    return {
      severity: "info",
      iconClass: "text-blue-800",
      text: `Neue Fassung ab ${
        splitTemporalCoverage(
          latestFutureVersion.value?.item.workExample.temporalCoverage || "",
        )[0] || ""
      }. `,
      linkText: "Zur zukünftigen Fassung",
      linkUrl: `/norms/${latestFutureVersion.value?.item.workExample.legislationIdentifier}`,
    };
  }
  if (status.value === "historical") {
    return {
      severity: "warn",
      iconClass: "scale-x-[-1]",
      text: "Historische Fassung. ",
      linkText: "Zur aktuell gültigen Fassung",
      linkUrl: `/norms/${inForceVersion.value?.item.workExample.legislationIdentifier}`,
    };
  }
  return {
    severity: "warn",
    iconClass: "",
    text: "Zukünftige Fassung. ",
    linkText: "Zur aktuell gültigen Fassung",
    linkUrl: `/norms/${inForceVersion.value?.item.workExample.legislationIdentifier}`,
  };
});
</script>

<template>
  <div v-if="status" class="mb-40 w-fit">
    <Message :severity="infoBoxProperties.severity" class="ris-body2-regular">
      <template #icon>
        <IcBaselineHistory :class="infoBoxProperties.iconClass" />
      </template>
      <p class="mt-2">
        <span class="ris-body2-bold"> {{ infoBoxProperties.text }} </span>
        <NuxtLink :to="infoBoxProperties.linkUrl">
          {{ infoBoxProperties.linkText }}
        </NuxtLink>
      </p>
    </Message>
  </div>
</template>
