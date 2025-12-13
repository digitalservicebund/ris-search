<script setup lang="ts">
import { NuxtLoadingIndicator } from "#components";
import ConsentBanner from "~/components/Analytics/ConsentBanner.vue";
import AppFooter from "~/components/app/AppFooter.vue";
import AppHeader from "~/components/app/AppHeader.vue";
</script>

<template>
  <NuxtLoadingIndicator :color="false" class="bg-blue-800" />

  <ConsentBanner />

  <div class="min-h-[50vh] bg-gray-100">
    <AppHeader />
    <main>
      <slot />
    </main>
  </div>

  <AppFooter />
</template>
