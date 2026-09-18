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
  title: "Datenschutzerklärung zu den Nutzungstests",
  description:
    "Erfahren Sie, wie wir Ihre Daten bei Teilnahme an unseren Nutzungstests erfassen, verwenden und schützen",
  ogTitle:
    "Datenschutzerklärung zu den Nutzungstests des Rechtsinformationsportals des Bundes",
});
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs
        :items="[
          {
            label:
              'Datenschutzerklärung für die Registrierung für Nutzungsstudien für das Projekt Testphase Rechtsinformationsportal',
          },
        ]"
      />
    </template>
  </NuxtLayout>
</template>
