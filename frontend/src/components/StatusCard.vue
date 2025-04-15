<script setup lang="ts">
import { StatusCardType } from "~/components/types";

interface Props {
  header: string;
  content: string;
  status: StatusCardType;
}

const props = defineProps<Props>();

const label = computed(() => {
  switch (props.status) {
    case StatusCardType.IMPLEMENTED:
      return {
        title: "Erste Version verfügbar",
        classes: "bg-green-100 text-green-800 border-1 border-green-200",
      };
    case StatusCardType.IN_PROGRESS:
      return {
        title: "In Arbeit",
        classes: "bg-yellow-100 text-orange-900 border-1 border-yellow-300",
      };
    case StatusCardType.PLANNED:
      return {
        title: "Geplant",
        classes: "bg-blue-200 text-blue-800 border-1 border-blue-200",
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
    <div v-if="label" class="mt-auto self-start py-4" :class="label.classes">
      <div class="ris-label3-bold px-8">
        {{ label.title }}
      </div>
    </div>
  </div>
</template>
