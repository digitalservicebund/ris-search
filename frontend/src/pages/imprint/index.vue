<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/impressum"], layout: false });

useStaticPageSeo(
  "Impressum",
  "Angaben gemäß § 5 TMG – Herausgeber, Verantwortliche und rechtliche Hinweise zum Portal.",
  "Impressum des Rechtsinformationsportals des Bundes",
);
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Impressum' }]" />
    </template>
  </NuxtLayout>
</template>
