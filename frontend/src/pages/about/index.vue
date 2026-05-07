<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/ueber"], layout: false });

useStaticPageSeo(
  "Über den Service",
  "Erfahren Sie, wie das Portal Gesetze und Urteile für alle frei zugänglich macht.",
  "Über das neue Rechtsinformationsportal des Bundes",
);
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Über den Service' }]" />
    </template>
  </NuxtLayout>
</template>
