<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{ href: string }>();
</script>
<template>
  <a :href="props.href" class="ds-button">
    <slot />
  </a>
</template>
