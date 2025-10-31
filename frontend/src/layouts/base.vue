<script setup lang="ts">
import { NuxtLoadingIndicator } from "#components";
import ConsentBanner from "~/components/Analytics/ConsentBanner.vue";
import AppFooter from "~/components/AppFooter.vue";
import AppHeader from "~/components/AppHeader.vue";
import NavbarTop from "~/components/NavbarTop.vue";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";

const privateFeaturesEnabled = usePrivateFeaturesFlag();
</script>

<template>
  <NuxtLoadingIndicator :color="false" class="bg-blue-800" />
  <client-only>
    <ConsentBanner />
  </client-only>
  <div class="flex flex-col gap-48">
    <div class="min-h-[50vh] bg-gray-100">
      <AppHeader v-if="privateFeaturesEnabled" />
      <NavbarTop v-else />
      <main>
        <slot />
      </main>
    </div>
    <AppFooter />
  </div>
</template>
