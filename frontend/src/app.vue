<script setup lang="ts">
import { useSentry } from "@/composables/useSentry";
useHead({
  titleTemplate: (pageTitle) =>
    pageTitle
      ? `${pageTitle} | Rechtsinformationen des Bundes`
      : "Rechtsinformationen des Bundes",
});
useSentry();
</script>

<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>
