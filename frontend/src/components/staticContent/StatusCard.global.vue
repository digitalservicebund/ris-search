<script setup lang="ts">
import type { BadgeColor } from "~/components/ui/Badge.vue";

export type StatusCardType = "implemented" | "in_progress" | "planned";

interface Props {
  status: StatusCardType;
}

const props = defineProps<Props>();

const badge = computed<{ label: string; color: BadgeColor } | undefined>(() => {
  switch (props.status) {
    case "implemented":
      return {
        label: "Erste Version verfügbar",
        color: "green",
      };
    case "in_progress":
      return {
        label: "In Arbeit",
        color: "yellow",
      };
    case "planned":
      return {
        label: "Geplant",
        color: "blue",
      };
    default:
      return undefined;
  }
});
</script>

<template>
  <div class="flex h-full flex-col bg-white p-16">
    <div class="typo-body-bold">
      <MDCSlot unwrap="p" name="header" />
    </div>
    <div class="typo-body-regular pt-8 pb-16">
      <MDCSlot unwrap="p" />
    </div>
    <div v-if="badge" class="mt-auto self-start">
      <UiBadge :label="badge.label" :color="badge.color" />
    </div>
  </div>
</template>
