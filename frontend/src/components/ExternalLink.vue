<script setup lang="ts">
import IcBaselineArrowOutward from "~icons/ic/baseline-arrow-outward";

const props = defineProps<{ url: string }>();
</script>

<template>
  <NuxtLink :to="props.url" target="_blank">
    <slot />
    <span class="sr-only">(öffnet in einem neuen Tab)</span>
    <IcBaselineArrowOutward
      class="mb-[0.5ch] ml-4 inline h-[1.2em] w-[1.2em]"
    />
  </NuxtLink>
</template>
