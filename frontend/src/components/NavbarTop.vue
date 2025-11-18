<script setup lang="ts">
import IconSearch from "~icons/ic/search?width=20&height=20";
</script>

<template>
  <nav
    class="flex items-center justify-between border-b border-gray-400 bg-white px-16 py-24"
    aria-label="Hauptnavigation"
  >
    <div class="flex items-center gap-44">
      <Logo />
      <NuxtLink
        class="ris-link1-regular link-hover flex items-center gap-4 decoration-3 aria-[current=page]:underline"
        to="/search"
        ><IconSearch />Suche</NuxtLink
      >
    </div>
  </nav>
</template>
