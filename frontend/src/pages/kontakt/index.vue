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
  title: "Kontakt",
  description:
    "Hier erreichen Sie uns bei Fragen, Hinweisen oder technischen Problemen rund um das Portal.",
  ogTitle: "Kontakt zum Rechtsinformationsportal des Bundes",
});
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Kontakt' }]" />
    </template>
  </NuxtLayout>
</template>
