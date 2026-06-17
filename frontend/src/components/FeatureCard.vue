<script setup lang="ts">
defineProps<{ innerClass?: string }>();
</script>

<template>
  <div
    class="flex flex-col gap-16 bg-white p-16 md:p-32 lg:px-64 lg:py-48"
    :class="innerClass"
  >
    <slot />
  </div>
</template>
