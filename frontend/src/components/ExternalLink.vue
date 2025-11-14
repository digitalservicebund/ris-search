<script setup lang="ts">
import IcBaselineArrowOutward from "~icons/ic/baseline-arrow-outward";
const props = defineProps<{ url: string }>();
</script>

<template>
  <NuxtLink :to="props.url" target="_blank">
    <slot />
    <IcBaselineArrowOutward
      class="ml-[0.5ch] inline h-[1em] w-[1em] align-baseline"
    />
  </NuxtLink>
</template>
