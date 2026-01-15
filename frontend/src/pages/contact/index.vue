<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/kontakt"], layout: false });

useStaticPageSeo("kontakt");
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Kontakt' }]" />
    </template>
  </NuxtLayout>
</template>
