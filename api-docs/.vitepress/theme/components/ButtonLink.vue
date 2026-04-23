<script setup lang="ts">
const props = defineProps<{ href: string }>();
</script>
<template>
  <a :href="props.href" class="ds-button">
    <slot />
  </a>
</template>
