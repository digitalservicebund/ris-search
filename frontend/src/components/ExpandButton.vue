<script setup lang="ts">
import GravityUiCircleChevronDownFill from "~icons/gravity-ui/circle-chevron-down-fill";
import GravityUiCircleChevronDown from "~icons/gravity-ui/circle-chevron-down";

const value = defineModel<boolean>();
const isHovered = ref(false);

const Icon = computed(() =>
  isHovered.value ? GravityUiCircleChevronDownFill : GravityUiCircleChevronDown,
);
</script>

<template>
  <button
    class="ris-body2-bold flex h-48 items-center gap-8 py-4 text-blue-800 outline-offset-4 outline-blue-800 hover:underline focus-visible:outline-4"
    @click="value = !value"
    @mouseover="isHovered = true"
    @mouseleave="isHovered = false"
  >
    <component
      :is="Icon"
      :data-open="value ? 'open' : null"
      class="data-open:rotate-180"
    /><slot />
  </button>
</template>
