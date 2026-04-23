<script setup lang="ts">
const props = defineProps<{ href: string }>();
</script>
<template>
  <a :href="props.href" v-bind="$attrs" class="ds-button">
    <slot />
  </a>
</template>
