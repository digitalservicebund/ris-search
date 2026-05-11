<script setup lang="ts">
const links = useSkipLinks([]);
</script>

<template>
  <div
    v-if="links.length"
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
  </div>
</template>
