<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ layout: false });

useStaticPageSeo("open-source");
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Open Source' }]" />
    </template>
  </NuxtLayout>
</template>
