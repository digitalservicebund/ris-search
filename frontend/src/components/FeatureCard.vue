<script setup lang="ts">
defineProps<{ innerClass?: string }>();
</script>

<template>
  <div
    class="flex flex-col gap-16 bg-white p-16 sm:p-24 md:gap-24 md:p-48 lg:px-80 2xl:px-96 2xl:py-56"
    :class="innerClass"
  >
    <slot />
  </div>
</template>
