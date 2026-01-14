<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ /* alias: ["/cookie-einstellungen"], */ layout: false });

useStaticPageSeo("cookies");
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Cookie-Einstellungen' }]" />
    </template>
  </NuxtLayout>
</template>
