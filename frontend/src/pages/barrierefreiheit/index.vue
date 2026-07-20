<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({
  layout: false,
  skipLinks: [
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
});

useSeo({
  title: "Barrierefreiheit",
  description:
    "Informationen zur digitalen Zugänglichkeit, zum technischen Standard und zur Feedback-Möglichkeit.",
  ogTitle: "Barrierefreiheit im Rechtsinformationsportal des Bundes",
});
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Barrierefreiheit' }]" />
    </template>
  </NuxtLayout>
</template>
