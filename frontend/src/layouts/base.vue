<script setup lang="ts">
import { NuxtLoadingIndicator } from "#components";
import ConsentBanner from "~/components/Analytics/ConsentBanner.vue";
import AppFooter from "~/components/AppFooter.vue";
import AppFooterPrototype from "~/components/AppFooterPrototype.vue";
import AppHeader from "~/components/AppHeader.vue";
import NavbarTop from "~/components/NavbarTop.vue";
import { useProfile } from "~/composables/useProfile";

const { isPublicProfile, isPrototypeProfile } = useProfile();
const showPublicProfileHeader = isPublicProfile() || isPrototypeProfile();
</script>

<template>
  <NuxtLoadingIndicator :color="false" class="bg-blue-800" />
  <client-only>
    <ConsentBanner />
  </client-only>
  <div class="flex flex-col gap-48">
    <div class="min-h-[50vh] bg-gray-100">
      <AppHeader v-if="showPublicProfileHeader" />
      <NavbarTop v-else />
      <main>
        <slot />
      </main>
    </div>
    <AppFooterPrototype v-if="true" />
    <AppFooter v-else />
  </div>
</template>
