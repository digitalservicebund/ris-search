<script setup lang="ts">
import PopoverNavigation from "./PopoverNavigation.vue";
import Banner from "~/components/Docs/Banner.vue";
</script>

<template>
  <header id="top" class="border-neutral-tertiary border-b-2">
    <Banner />
    <div class="flex flex-wrap items-center justify-between gap-24">
      <PopoverNavigation />
    </div>
  </header>
</template>
