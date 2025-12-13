<script setup lang="ts">
import { NuxtLoadingIndicator } from "#components";
import ConsentBanner from "~/components/analytics/ConsentBanner.vue";
import Banner from "~/components/apiDocs/Banner.vue";
import MobileNav from "~/components/apiDocs/MobileNav.vue";
import Sidebar from "~/components/apiDocs/Sidebar.vue";
import AppFooter from "~/components/app/AppFooter.vue";
</script>

<template>
  <NuxtLoadingIndicator :color="false" class="bg-blue-800" />
  <ConsentBanner />
  <div>
    <Sidebar
      class="fixed hidden h-full w-[252px] border-r border-gray-400 lg:inline-block"
    />
    <MobileNav class="lg:hidden" />
    <div class="flex flex-col gap-32 lg:ml-[252px] lg:w-[calc(100%-252px)]">
      <div class="min-h-screen dark:text-white">
        <div class="bg-background-primary top-0 z-40">
          <Banner class="hidden lg:block" />
        </div>
        <article class="container pt-32">
          <div class="prose max-w-prose">
            <slot />
          </div>
        </article>
      </div>
      <AppFooter />
    </div>
  </div>
</template>
