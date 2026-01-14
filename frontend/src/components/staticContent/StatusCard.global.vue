<script setup lang="ts">
import Badge, { BadgeColor } from "~/components/Badge.vue";

export type StatusCardType = "implemented" | "in_progress" | "planned";

interface Props {
  status: StatusCardType;
}

const props = defineProps<Props>();

const badge = computed(() => {
  switch (props.status) {
    case "implemented":
      return {
        label: "Erste Version verfügbar",
        color: BadgeColor.GREEN,
      };
    case "in_progress":
      return {
        label: "In Arbeit",
        color: BadgeColor.YELLOW,
      };
    case "planned":
      return {
        label: "Geplant",
        color: BadgeColor.BLUE,
      };
    default:
      return undefined;
  }
});
</script>

<template>
  <div class="flex h-full flex-col bg-white p-16">
    <div class="ris-body2-bold">
      <MDCSlot unwrap="p" name="header" />
    </div>
    <div class="ris-body2-regular pt-8 pb-16">
      <MDCSlot unwrap="p" />
    </div>
    <div v-if="badge" class="mt-auto self-start">
      <Badge :label="badge.label" :color="badge.color" />
    </div>
  </div>
</template>
