<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ layout: false });

useStaticPageSeo(
  "Feedback",
  "Teilen Sie Ihre Rückmeldungen und Anregungen zur Testphase – Ihr Input hilft bei der Weiterentwicklung.",
  "Feedback zum Rechtsinformationsportal des Bundes",
);
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Feedback' }]" />
    </template>
  </NuxtLayout>
</template>
