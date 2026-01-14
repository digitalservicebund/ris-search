<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ /* alias: ["/impressum"], */ layout: false });

useStaticPageSeo("impressum");
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Impressum' }]" />
    </template>
  </NuxtLayout>
</template>
