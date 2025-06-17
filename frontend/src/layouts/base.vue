<script setup lang="ts">
import AppFooter from "@/components/AppFooter.vue";
import ConsentBanner from "~/components/Analytics/ConsentBanner.vue";
import AppHeader from "@/components/AppHeader.vue";
import NavbarTop from "@/components/NavbarTop.vue";
import { isPublicProfile, isPrototypeProfile } from "@/utils/config";
import AppFooterPrototype from "~/components/AppFooterPrototype.vue";
/* v8 ignore start */
const isPublic = isPublicProfile();
const isPrototype = isPrototypeProfile();
const showPublicProfileHeader = isPublic || isPrototype;
/* v8 ignore stop */
</script>

<template>
  <client-only>
    <ConsentBanner />
  </client-only>
  <div class="flex flex-col gap-48">
    <div class="min-h-[50vh] bg-gray-100">
      <AppHeader v-if="showPublicProfileHeader" />
      <NavbarTop v-else />
      <slot />
    </div>
    <AppFooterPrototype v-if="true" />
    <AppFooter v-else />
  </div>
</template>
