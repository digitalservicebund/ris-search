<script setup lang="ts">
defineProps<{ innerClass?: string }>();
</script>
<template>
  <div class="ris-body1-regular mx-auto max-w-prose lg:px-16">
    <div
      class="flex flex-col gap-16 bg-white p-16 sm:px-16 sm:py-16 md:-mx-32 md:p-32 lg:-mx-64 lg:px-64 lg:py-48"
      :class="innerClass"
    >
      <slot />
    </div>
  </div>
</template>
