<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({ /* alias: ["/einfuehrung"], */ layout: false });
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs :items="[{ label: 'Einführende Informationen' }]" />
    </template>
  </NuxtLayout>
</template>
