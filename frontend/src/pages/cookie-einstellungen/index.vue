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
  title: "Cookie-Einstellungen",
  description:
    "Wählen Sie, welche Cookies Sie zulassen – für eine bessere, datenschutzgerechte Nutzung.",
  ogTitle: "Cookie-Einstellungen für das Rechtsinformationsportal des Bundes",
});
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Cookie-Einstellungen' }]" />
    </template>
  </NuxtLayout>
</template>
