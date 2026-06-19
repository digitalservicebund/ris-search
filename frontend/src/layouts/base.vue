<script setup lang="ts">
import { NuxtLoadingIndicator } from "#components";
import ConsentBanner from "~/components/analytics/ConsentBanner.vue";
</script>

<template>
  <NuxtLoadingIndicator :color="false" class="bg-blue-800" />

  <ConsentBanner />

  <div class="min-h-[50vh] bg-gray-100">
    <AppHeader />
    <main id="main">
      <slot />
    </main>
  </div>

  <AppFooter />
</template>
