<script setup lang="ts">
const links = useSkipLinks();
</script>

<template>
  <nav
    v-if="links?.length"
    aria-label="Sprunglinks"
    class="pointer-events-none fixed inset-x-0 top-8 z-10 flex justify-center gap-8 px-8"
  >
    <SkipLink
      v-for="link in links"
      :key="link.label"
      :to="link.to"
      class="pointer-events-auto"
    >
      {{ link.label }}
    </SkipLink>
  </nav>
</template>
