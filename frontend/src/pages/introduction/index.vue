<script setup lang="ts">
definePageMeta({ alias: ["/einfuehrung"], layout: false });
</script>

<template>
  <div>
    <NuxtLayout name="static-page">
      <template #breadcrumb>
        <RisBreadcrumb :items="[{ label: 'Einführende Informationen' }]" />
      </template>

      <template #pageTitle>Einführende Informationen</template>
    </NuxtLayout>
  </div>
</template>
