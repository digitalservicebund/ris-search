<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{ tint?: "blue" | "green" | "gray" }>();
let tint = computed(() => {
  if (props.tint === "blue") {
    return ["border-transparent", "bg-blue-200", "text-brand-primary"];
  } else if (props.tint === "green") {
    return ["border-transparent", "bg-green-100", "text-green-800"];
  } else if (props.tint === "gray") {
    return ["border-transparent", "bg-gray-100", "text-gray-800"];
  } else {
    return ["border-gray-100"];
  }
});
</script>

<template>
  <code
    class="py-2 px-6 inline-block rounded border text-ellipsis overflow-hidden whitespace-nowrap"
    :class="[...tint]"
  >
    <slot />
  </code>
</template>
