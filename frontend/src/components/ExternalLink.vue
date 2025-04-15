<script setup lang="ts">
import IcBaselineArrowOutward from "~icons/ic/baseline-arrow-outward?width=1em&height=1em";
const props = defineProps<{ url: string }>();
</script>

<template>
  <a :href="props.url" rel="noopener" target="_blank">
    <slot /><IcBaselineArrowOutward class="ml-[0.5ch] inline align-baseline" />
  </a>
</template>
