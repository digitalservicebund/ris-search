<script setup lang="ts">
useHead({
  titleTemplate: (pageTitle) =>
    pageTitle
      ? `${pageTitle} — Rechtsinformationen des Bundes`
      : "Rechtsinformationen des Bundes",
});
</script>

<template>
  <AppSkipLinks />
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>
