<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/impressum"], layout: false });

useSeo({
  title: "Impressum",
  description:
    "Angaben gemäß § 5 TMG – Herausgeber, Verantwortliche und rechtliche Hinweise zum Portal.",
  ogTitle: "Impressum des Rechtsinformationsportals des Bundes",
});
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Impressum' }]" />
    </template>
  </NuxtLayout>
</template>
