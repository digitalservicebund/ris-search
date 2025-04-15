<script setup lang="ts">
defineProps<{ border?: boolean }>();
</script>

<template>
  <div
    class="pt-20 sm:pt-32 lg:pt-64"
    :class="{ 'border-b border-b-gray-400': border }"
  >
    <slot />
  </div>
</template>
