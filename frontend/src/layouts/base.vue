<script setup lang="ts">
import { NuxtLoadingIndicator } from "#components";
import ConsentBanner from "~/components/analytics/ConsentBanner.vue";
import AppFooter from "~/components/app/AppFooter.vue";
import AppHeader from "~/components/app/AppHeader.vue";

const route = useRoute();

const skipLinks = provideSkipLinks();

watch(
  () => route.path,
  () => {
    skipLinks.clear();
  },
);
</script>

<template>
  <NuxtLoadingIndicator :color="false" class="bg-blue-800" />

  <AppSkipLinks />
  <ConsentBanner />

  <div class="min-h-[50vh] bg-gray-100">
    <AppHeader />
    <main id="main">
      <slot />
    </main>
  </div>

  <AppFooter />
</template>
