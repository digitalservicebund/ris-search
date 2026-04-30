<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/nutzungstests"], layout: false });

useStaticPageSeo("nutzungstests");
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Nutzungstests' }]" />
    </template>
  </NuxtLayout>
</template>
