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
  title: "Open Source",
  description:
    "Informationen zur verwendeten Open-Source-Software, zu Lizenzen und Beteiligungsmöglichkeiten.",
  ogTitle: "Open Source im Rechtsinformationsportal des Bundes",
});
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Open Source' }]" />
    </template>
  </NuxtLayout>
</template>
