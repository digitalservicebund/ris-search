<script setup lang="ts">
interface Props {
  title?: string;
  placeholder: string;
}

const { title, placeholder } = defineProps<Props>();
</script>

<template>
  <h1
    class="ris-heading2-bold max-w-title mt-24 mb-48 text-balance wrap-break-word hyphens-auto max-sm:text-2xl"
    :class="{ 'text-gray-900': !title }"
  >
    {{ title ?? placeholder }}
  </h1>
</template>
