<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ layout: false });

useStaticPageSeo("feedback");
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Feedback' }]" />
    </template>
  </NuxtLayout>
</template>
