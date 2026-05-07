<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({
  alias: ["/nutzungstests-datenschutz"],
  layout: false,
});

useStaticPageSeo(
  "Datenschutzerklärung zu den Nutzungstests",
  "Erfahren Sie, wie wir Ihre Daten bei Teilnahme an unseren Nutzungstests erfassen, verwenden und schützen",
  "Datenschutzerklärung zu den Nutzungstests des Rechtsinformationsportals des Bundes",
);
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
