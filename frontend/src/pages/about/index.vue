<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/ueber"], layout: false });

useSeo({
  title: "Über den Service",
  description:
    "Erfahren Sie, wie das Portal Gesetze und Urteile für alle frei zugänglich macht.",
  ogTitle: "Über das neue Rechtsinformationsportal des Bundes",
});
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Über den Service' }]" />
    </template>
  </NuxtLayout>
</template>
