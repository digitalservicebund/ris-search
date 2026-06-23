<script setup lang="ts">
interface Props {
  title?: string;
  placeholder: string;
}

const { title, placeholder } = defineProps<Props>();
</script>

<template>
  <h1
    class="typo-headline2-bold mb-8 wrap-break-word hyphens-auto sm:mb-16 md:mb-24"
    :class="{ 'text-gray-900': !title }"
  >
    {{ title ?? placeholder }}
  </h1>
</template>
