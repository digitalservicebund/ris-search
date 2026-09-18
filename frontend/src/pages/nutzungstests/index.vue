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
  title: "Nutzungstests",
  description:
    "Erfahren Sie, wie das Portal getestet wird, welche Ergebnisse vorliegen und wie Sie teilnehmen können.",
  ogTitle: "Nutzungstests zum Rechtsinformationsportal des Bundes",
});
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Nutzungstests' }]" />
    </template>
  </NuxtLayout>
</template>
