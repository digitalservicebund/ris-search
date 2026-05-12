<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/datenschutz"], layout: false });

useStaticPageSeo({
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
