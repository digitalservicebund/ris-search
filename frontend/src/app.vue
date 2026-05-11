<script setup lang="ts">
import { Toast } from "primevue";

const route = useRoute();
const skipLinks = provideSkipLinks();

useHead({
  titleTemplate: (pageTitle) =>
    pageTitle
      ? `${pageTitle} — Rechtsinformationen des Bundes`
      : "Rechtsinformationen des Bundes",
});

watch(
  () => route.path,
  () => {
    skipLinks.clear();
  },
  { immediate: true },
);
</script>

<template>
  <AppSkipLinks />
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
  <Toast />
</template>
