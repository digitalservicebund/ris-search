<script setup lang="ts">
import Toast from "primevue/toast";

useHead({
  titleTemplate: (pageTitle) =>
    pageTitle
      ? `${pageTitle} | Rechtsinformationen des Bundes`
      : "Rechtsinformationen des Bundes",
});
</script>

<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
  <Toast />
</template>
