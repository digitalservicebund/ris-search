<script setup lang="ts">
import { computed } from "vue";
import Icon from "./Icon.vue";
import CodeBadge from "./CodeBadge.vue";

const props = defineProps<{
  item: string;
  type: "folder" | "file";
  level: string;
}>();

const icon = computed(() => (props.type === "folder" ? "folder" : "note"));
const level = computed(() => parseInt(props.level));
</script>

<template>
  <div
    class="px-8 py-6 flex flex-row gap-4 items-start"
    :class="{
      'pl-12 md:pl-24': level === 2,
      'pl-24 md:pl-48': level === 3,
    }"
  >
    <div class="w-20 h-20">
      <Icon :id="icon" />
    </div>
    <div class="flex flex-col gap-4 flex-grow justify-between">
      <dt class="">
        <CodeBadge tint="gray">{{ props.item }}</CodeBadge>
      </dt>
      <dd class="text-gray-800 text-sm">
        <slot />
      </dd>
    </div>
  </div>
</template>
