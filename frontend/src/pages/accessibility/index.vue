<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/barrierefreiheit"], layout: false });

useStaticPageSeo("barrierefreiheit");
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Barrierefreiheit' }]" />
    </template>
  </NuxtLayout>
</template>
