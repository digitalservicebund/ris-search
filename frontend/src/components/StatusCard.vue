<script setup lang="ts">
import { StatusCardType } from "~/components/types";
import Badge, { BadgeColor } from "@/components/Badge.vue";

interface Props {
  header: string;
  content: string;
  status: StatusCardType;
}

const props = defineProps<Props>();

const badge = computed(() => {
  switch (props.status) {
    case StatusCardType.IMPLEMENTED:
      return {
        label: "Erste Version verfügbar",
        color: BadgeColor.GREEN,
      };
    case StatusCardType.IN_PROGRESS:
      return {
        label: "In Arbeit",
        color: BadgeColor.YELLOW,
      };
    case StatusCardType.PLANNED:
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
      {{ props.header }}
    </div>
    <div class="ris-body2-regular pt-8 pb-16">
      {{ props.content }}
    </div>
    <div v-if="badge" class="mt-auto self-start">
      <Badge :label="badge.label" :color="badge.color" />
    </div>
  </div>
</template>
