<script setup lang="ts">
defineProps<{ title?: string }>();
</script>

<template>
  <div class="py-20 sm:pt-24 sm:pb-48">
    <h1
      class="ris-heading1-regular inline-block hyphens-auto max-sm:leading-48"
    >
      {{ title }}<slot name="inline"></slot>
    </h1>
    <div v-if="$slots.default" class="mt-16">
      <slot />
    </div>
  </div>
</template>
