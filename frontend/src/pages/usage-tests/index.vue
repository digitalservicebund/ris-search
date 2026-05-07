<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/nutzungstests"], layout: false });

useStaticPageSeo(
  "Nutzungstests",
  "Erfahren Sie, wie das Portal getestet wird, welche Ergebnisse vorliegen und wie Sie teilnehmen können.",
  "Nutzungstests zum Rechtsinformationsportal des Bundes",
);
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Nutzungstests' }]" />
    </template>
  </NuxtLayout>
</template>
