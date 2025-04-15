<script setup lang="ts">
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
</template>
