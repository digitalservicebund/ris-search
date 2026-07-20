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
  title: "Datenschutzerklärung",
  description:
    "Wie wir Ihre Daten schützen, welche Rechte Sie haben und welche Verfahren wir anwenden.",
  ogTitle: "Datenschutzrichtlinie des Rechtsinformationsportals des Bundes",
});
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Datenschutzerklärung' }]" />
    </template>
  </NuxtLayout>
</template>
