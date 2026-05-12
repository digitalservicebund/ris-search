<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ layout: false });

useSeo({
  title: "Feedback",
  description:
    "Teilen Sie Ihre Rückmeldungen und Anregungen zur Testphase – Ihr Input hilft bei der Weiterentwicklung.",
  ogTitle: "Feedback zum Rechtsinformationsportal des Bundes",
});
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Feedback' }]" />
    </template>
  </NuxtLayout>
</template>
