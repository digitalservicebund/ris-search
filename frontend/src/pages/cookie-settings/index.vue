<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/cookie-einstellungen"], layout: false });

useStaticPageSeo(
  "Cookie-Einstellungen",
  "Wählen Sie, welche Cookies Sie zulassen – für eine bessere, datenschutzgerechte Nutzung.",
  "Cookie-Einstellungen für das Rechtsinformationsportal des Bundes",
);
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Cookie-Einstellungen' }]" />
    </template>
  </NuxtLayout>
</template>
