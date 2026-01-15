<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/datenschutz"], layout: false });

useStaticPageSeo("datenschutz");
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Datenschutzerklärung' }]" />
    </template>
  </NuxtLayout>
</template>
