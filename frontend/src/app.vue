<script setup lang="ts">
import { Toast } from "primevue";

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
  <Toast />
</template>
