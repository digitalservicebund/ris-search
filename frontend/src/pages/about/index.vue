<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ alias: ["/ueber"], layout: false });

useStaticPageSeo("ueber");
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Über den Service' }]" />
    </template>
  </NuxtLayout>
</template>
